const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();
app.use(bodyParser.json());

const Routes = require('./routes/routes');
app.use('/nutriplanner', Routes);

app.get('/currentDateTime', (req, res) => {
    const now = new Date();
    const currentDateTime = now.toISOString().slice(0, 19); 
    res.status(200).send({ datetime: currentDateTime });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}/`);
});
