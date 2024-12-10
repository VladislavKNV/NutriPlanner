const mysql = require("mysql2/promise");
const nodemailer = require('nodemailer');
const crypto = require('crypto');

const pool = mysql.createPool({
    host: '127.0.0.1',
    port: 3306,
    user: 'your_db_user',
    password: 'your_db_password',
    database: 'your_database_name',
    waitForConnections: true,
    connectionLimit: 50,
    queueLimit: 0
 });

 const transporter = nodemailer.createTransport({
    host: 'smtp.your-email-provider.com',
    port: 465,
    secure: true,
    auth: {
       user: "your_email@example.com",
       pass: "your_email_password"
    }
 });

async function forgotPassword(req, res) {
    const { email } = req.body;
    if (!email) return res.status(400).send({ error: 'Email is required' });

    try {
        const connection = await pool.getConnection();
        try {
            const [users] = await connection.query('SELECT * FROM NP_USERS WHERE email = ?', [email]);
            if (users.length === 0) return res.status(404).send({ error: 'User not found' });

            const tempPassword = Math.random().toString(36).substring(2, 10);
            const hashedPassword = crypto.createHash('sha256').update(tempPassword).digest('hex');

            const [updateResult] = await connection.query('UPDATE NP_USERS SET password = ? WHERE email = ?', [hashedPassword, email]);
            if (updateResult.affectedRows === 0) return res.status(500).send({ error: 'Failed to update the password in the database' });

            const mailOptions = {
                from: 'mr.seny4@yandex.by',
                to: email,
                subject: 'Password Recovery in NutriPlanner',
                html: `
                <p>Здравствуйте!</p>
                <p>Ваш временный пароль:</p>
                <p><strong>${tempPassword}</strong></p>
                <p>Пожалуйста, используйте этот пароль для входа в систему и незамедлительно измените его на новый для обеспечения безопасности вашей учётной записи.</p>
                <p>Если вы не запрашивали восстановление пароля, проигнорируйте это письмо или свяжитесь с нашей службой поддержки.</p>
                <p>С уважением,<br>Команда NutriPlanner</p>
                `,
            };
            await transporter.sendMail(mailOptions);
            res.status(200).send({ message: 'A temporary password has been sent to your email.' });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error in forgotPassword route:', error);
        res.status(500).send({ error: 'An error occurred while processing the request.' });
    }
}

async function register(req, res) {
    const {
        roleId, name, email, password, gender, birthDate, goal, desiredWeight,
        activityLevel, weightFactor, registrationDate, lastLoginDate,
        height, currentWeight, measurementDate
    } = req.body;

    const params = [roleId ?? null, name ?? null, email ?? null, password ?? null, gender ?? null, birthDate ?? null, goal ?? null, desiredWeight ?? null, activityLevel ?? null, weightFactor ?? null, registrationDate ?? new Date(), lastLoginDate ?? new Date(), height ?? null, currentWeight ?? null, measurementDate ?? null];

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(`CALL AddUserWithBMI(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`, params);
            res.status(200).send({ message: 'User and BMI data added successfully' });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error while executing procedure:', error);
        res.status(500).send({ error: 'An error occurred while adding user' });
    }
}

async function login(req, res) {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).send({ error: 'Email and password are required' });

    try {
        const connection = await pool.getConnection();
        try {
            const [users] = await connection.execute(`SELECT * FROM NP_USERS WHERE email = ? AND password = ?`, [email, password]);
            if (users.length === 0) return res.status(401).send({ error: 'Incorrect credentials' });

            const user = users[0];
            const [bmiData] = await connection.execute(`SELECT * FROM NP_BMI WHERE userId = ?`, [user.id]);
            const [favoriteFoodData] = await connection.execute(`SELECT * FROM NP_FAVORITE_FOOD WHERE userId = ?`, [user.id]);
            const [foodIntakeData] = await connection.execute(`SELECT * FROM NP_FOOD_INTAKE WHERE userId = ?`, [user.id]);
            const [foodsData] = await connection.execute(`SELECT * FROM NP_FOODS`);

            const response = {
                user: user,
                bmi: bmiData,
                favorite_food: favoriteFoodData,
                food_intake: foodIntakeData,
                foods: foodsData
            };

            res.status(200).send(response);
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error executing request:', error);
        res.status(500).send({ error: 'An error occurred while logging in' });
    }
}

async function updateLastLogin(req, res) {
    const { idUser, lastLoginDate } = req.body;

    if (idUser == null || lastLoginDate == null) {
        return res.status(400).send({ error: 'userId and lastLoginDate are required' });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `UPDATE NP_USERS SET lastLoginDate = ? WHERE id = ?`,
                [lastLoginDate, idUser]
            );

            if (result.affectedRows === 0) {
                return res.status(404).send({ error: 'User not found' });
            }
            res.status(200).send({ message: 'Last login date updated successfully' });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error updating last login date: ', error);
        res.status(500).send({ error: 'Error updating last login date' });
    }
}

async function addOrUpdateBMI(req, res) {
    const { userId, height, currentWeight, measurementDate } = req.body;

    if (!userId || !height || !currentWeight || !measurementDate) {
        return res.status(400).send({
            error: "userId, height, currentWeight, and measurementDate are required",
        });
    }

    try {
        const connection = await pool.getConnection();
        try {
            await connection.query(
                `CALL AddOrUpdateBMI(?, ?, ?, ?, @returnedId)`,
                [userId, height, currentWeight, measurementDate]
            );

            const [returnedIdResult] = await connection.query(
                `SELECT @returnedId AS id`
            );

            const returnedId = returnedIdResult[0] ? returnedIdResult[0].id : null;
            
            if (returnedId !== null) {
                res.status(200).send({
                    message: "BMI record added or updated successfully",
                    id: returnedId,
                });
            } else {
                res.status(500).send({
                    error: "Failed to retrieve the ID of the added or updated record",
                });
            }
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error("Error while calling AddOrUpdateBMI procedure:", error);
        res.status(500).send({ error: "An error occurred while adding or updating BMI" });
    }
}

async function updateUserInfo(req, res) {
    const {
        idUser, name, goal, desiredWeight,
        activityLevel, weightFactor
    } = req.body;

    if (idUser == null, name == null, goal == null, desiredWeight == null, activityLevel == null, weightFactor == null) {
        return res.status(400).send({ error: 'Not enough data' });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `UPDATE NP_USERS 
                 SET name = ?, goal = ?, desiredWeight = ?, activityLevel = ?, weightFactor = ?
                 WHERE id = ?`,
                [name, goal, desiredWeight, activityLevel, weightFactor, idUser]
            );

            if (result.affectedRows === 0) {
                return res.status(404).send({ error: 'User not found' });
            }

            res.status(200).send({ message: 'User information has been successfully updated' });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error updating user information:', error);
        res.status(500).send({ error: 'Error updating user information' });
    }
}

async function updatePassword(req, res) {
    const { idUser, oldPassword, newPassword } = req.body;

    if (idUser == null || oldPassword == null || newPassword == null) {
        return res.status(400).send({ error: 'idUser, oldPassword and newPassword are required' });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [rows] = await connection.execute(
                `SELECT password FROM NP_USERS WHERE id = ?`,
                [idUser]
            );

            if (rows.length === 0) {
                return res.status(404).send({ error: 'User not found' });
            }

            const currentPassword = rows[0].password;

            if (currentPassword !== oldPassword) {
                return res.status(400).send({ error: 'oldPassword is incorrect' });
            }

            const [updateResult] = await connection.execute(
                `UPDATE NP_USERS SET password = ? WHERE id = ?`,
                [newPassword, idUser]
            );

            if (updateResult.affectedRows === 0) {
                return res.status(500).send({ error: 'Failed to update password' });
            }
            res.status(200).send({ message: 'Password updated successfully' });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error updating password:', error);
        res.status(500).send({ error: 'Error updating password' });
    }
}

async function addFood(req, res) {
    const {
        foodName, description, category, foodRating, ingredients, recipe,
        caloriesPer100g, carbohydrates, fats, proteins, servingSize
    } = req.body;

    if (!foodName || !category || foodRating  == null || fats  == null || proteins  == null || carbohydrates  == null || caloriesPer100g == null) {
        return res.status(400).send({ error: 'foodName, category, proteins, fats, carbohydrates, caloriesPer100g and foodRating are required' });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `INSERT INTO NP_FOODS (foodName, description, category, foodRating, ingredients, recipe, caloriesPer100g, carbohydrates, fats, proteins, servingSize) 
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
                [foodName, description, category, foodRating, ingredients, recipe, caloriesPer100g, carbohydrates, fats, proteins, servingSize]
            );

            res.status(201).send({ message: 'Food item successfully added', foodId: result.insertId });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error adding food item:', error);
        res.status(500).send({ error: 'An error occurred while adding the food item' });
    }
}

async function updateFood(req, res) {
    const foodId = req.params.id;
    const {
        foodName, description, category, foodRating, ingredients, recipe,
        caloriesPer100g, carbohydrates, fats, proteins, servingSize
    } = req.body;

    if (!foodName || !category || foodRating  == null || fats  == null || proteins  == null || carbohydrates  == null || caloriesPer100g == null) {
        return res.status(400).send({ error: 'foodName, category, proteins, fats, carbohydrates, caloriesPer100g and foodRating are required' });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `UPDATE NP_FOODS 
                 SET foodName = ?, description = ?, category = ?, foodRating = ?, ingredients = ?, recipe = ?, caloriesPer100g = ?, carbohydrates = ?, fats = ?, proteins = ?, servingSize = ?
                 WHERE id = ?`,
                [foodName, description, category, foodRating, ingredients, recipe, caloriesPer100g, carbohydrates, fats, proteins, servingSize, foodId]
            );

            if (result.affectedRows === 0) {
                return res.status(404).send({ error: 'Food item not found' });
            }

            res.status(200).send({ message: 'Food item successfully updated' });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error updating food item:', error);
        res.status(500).send({ error: 'An error occurred while updating the food item' });
    }
}

async function deleteFood(req, res) {
    const foodId = req.params.id;

    if (!foodId) {
        return res.status(400).send({ error: 'foodId is required' });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `DELETE FROM NP_FOODS WHERE id = ?`,
                [foodId]
            );

            if (result.affectedRows === 0) {
                return res.status(404).send({ error: 'Food item not found' });
            }

            res.status(200).send({ message: 'Food item successfully deleted' });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error deleting food item:', error);
        res.status(500).send({ error: 'An error occurred while deleting the food item' });
    }
}

async function getFoods(req, res) {
    try {
        const connection = await pool.getConnection();
        try {
            const [rows] = await connection.execute(`SELECT * FROM NP_FOODS`);
            res.status(200).send(rows);
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error while fetching foods:', error);
        res.status(500).send({ error: 'An error occurred while fetching the food items' });
    }
}

async function addFavoriteFood(req, res) {
    const { userId, foodId } = req.body;

    if (!userId || !foodId) {
        return res.status(400).send({ error: "userId and foodId are required" });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `INSERT INTO NP_FAVORITE_FOOD (userId, foodId) VALUES (?, ?)`,
                [userId, foodId]
            );
            res.status(201).send({ 
                message: "Favorite food added successfully",
                idfavoritefood: result.insertId, 
            });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error("Error while adding favorite food:", error);
        res.status(500).send({ error: "An error occurred while adding favorite food" });
    }
}

async function deleteFavoriteFood(req, res) {
    const { id } = req.params;

    if (!id) {
        return res.status(400).send({ error: "Favorite food ID is required" });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `DELETE FROM NP_FAVORITE_FOOD WHERE id = ?`,
                [id]
            );
            
            if (result.affectedRows === 0) {
                return res.status(404).send({ error: "Favorite food not found" });
            }
            res.status(200).send({ 
                message: "Favorite food deleted successfully",
                idfavoritefood: result.insertId
            });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error("Error while deleting favorite food:", error);
        res.status(500).send({ error: "An error occurred while deleting favorite food" });
    }
}

async function addFoodIntake(req, res) {
    const { userId, foodId, mealType, date } = req.body;

    if (!userId || !foodId || !mealType || !date) {
        return res.status(400).send({ error: "userId, foodId, mealType, and date are required" });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `INSERT INTO NP_FOOD_INTAKE (userId, foodId, mealType, date) VALUES (?, ?, ?, ?)`,
                [userId, foodId, mealType, date]
            );

            res.status(201).send({
                message: "Food intake added successfully",
                idfoodintake: result.insertId,
            });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error("Error while adding food intake:", error);
        res.status(500).send({ error: "An error occurred while adding food intake" });
    }
}

async function deleteFoodIntake(req, res) {
    const { id } = req.params;

    if (!id) {
        return res.status(400).send({ error: "Food intake ID is required" });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [result] = await connection.execute(
                `DELETE FROM NP_FOOD_INTAKE WHERE id = ?`,
                [id]
            );

            if (result.affectedRows === 0) {
                return res.status(404).send({ error: "Food intake not found" });
            }

            res.status(200).send({ message: "Food intake deleted successfully" });
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error("Error while deleting food intake:", error);
        res.status(500).send({ error: "An error occurred while deleting food intake" });
    }
}

async function checkEmail(req, res) {
    const { email } = req.body;

    if (!email) {
        return res.status(400).send({ message: 'Email is required' });
    }

    try {
        const connection = await pool.getConnection();
        try {
            const [users] = await connection.query(
                'SELECT * FROM NP_USERS WHERE email = ?',
                [email]
            );

            if (users.length > 0) {
                res.status(404).send({ message: 'User exists' });
            } else {
                res.status(200).send({ message: 'User not found' });
            }
        } finally {
            connection.release();
        }
    } catch (error) {
        console.error('Error in checkEmail route:', error);
        res.status(500).send({ message: 'An error occurred while checking the email' });
    }
}


module.exports = {
    forgotPassword,
    register,
    login,
    updateLastLogin,
    addOrUpdateBMI,
    updateUserInfo,
    updatePassword,
    addFood,
    updateFood,
    deleteFood,
    getFoods,
    addFavoriteFood,
    deleteFavoriteFood,
    addFoodIntake,
    deleteFoodIntake,
    checkEmail
};

