const Koa = require('koa');
const cors = require('@koa/cors');
const bodyParser = require('koa-bodyparser');

const indexRoutes = require('./routes/index');
const queuesRoutes = require('./routes/queues');

const app = new Koa();
const PORT = process.env.PORT || 1338;

app.use(bodyParser());
app.use(indexRoutes.routes());
app.use(queuesRoutes.routes());

let server;
if (process.env.ALWAYSDATA_HTTPD_PORT && process.env.ALWAYSDATA_HTTPD_IP) {
    server = app.listen(process.env.ALWAYSDATA_HTTPD_PORT, process.env.ALWAYSDATA_HTTPD_IP, () => {
        console.log(`Server is running`);
    });
} else {
    server = app.listen(PORT, () => {
        console.log(`Server is running`);
    });
}

module.exports = server;
