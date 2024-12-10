const express = require("express");
const router = express.Router();
const handlers = require('./handlers'); 

router.post("/forgotPassword", handlers.forgotPassword);
router.post("/register", handlers.register);
router.post("/login", handlers.login);
router.put("/updateLastLogin", handlers.updateLastLogin);
router.post("/addOrUpdateBMI", handlers.addOrUpdateBMI);
router.put("/updateUserInfo", handlers.updateUserInfo);
router.put("/updatePassword", handlers.updatePassword);
router.post("/addFood", handlers.addFood);
router.put("/updateFood/:id", handlers.updateFood);
router.delete("/deleteFood/:id", handlers.deleteFood);
router.get("/getFoods", handlers.getFoods);
router.post("/addFavoriteFood", handlers.addFavoriteFood);
router.delete("/deleteFavoriteFood/:id", handlers.deleteFavoriteFood);
router.post("/addFoodIntake", handlers.addFoodIntake);
router.delete("/deleteFoodIntake/:id", handlers.deleteFoodIntake);
router.post("/checkEmail", handlers.checkEmail);


module.exports = router;
