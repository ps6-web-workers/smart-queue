const Router = require('koa-router');
const queries = require('../db/queries/queues');

const router = new Router();
const BASE_URL = `/api/queues`;

module.exports = router;

// get all the queues
router.get(BASE_URL, async (ctx) => {
    try {
        const queues = await queries.getAllQueues();
        ctx.status = 200;
        ctx.body = {
            status: 'success',
            data: { queues: queues },
        };
    } catch (e) {
        console.log(e);
        ctx.status = 500;
        ctx.body = {
            status: 'error',
            data: e.message,
        };
    }
});
