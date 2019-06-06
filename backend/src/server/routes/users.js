const Router = require('koa-router');
const queries = require('../db/queries/queues');
const utils = require('./routes-utils');

const router = new Router();
const BASE_URL = `/api/users`;

module.exports = router;

// get user's info
router.get(`${BASE_URL}/:id`, async (ctx) => {
    try {
        const queues = await queries.getAllQueues();
        utils.success(ctx, {data: {queues: queues}});
    } catch (e) {
        utils.failure(ctx);
    }
});
