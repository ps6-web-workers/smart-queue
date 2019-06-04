const Router = require('koa-router');
const queries = require('../db/queries/queues');
const utils = require('./routes-utils');

const router = new Router();
const BASE_URL = `/api/queues`;

module.exports = router;

// get all the queues
router.get(BASE_URL, async (ctx) => {
    try {
        const queues = await queries.getAllQueues();
        utils.success(ctx, queues);
    } catch (e) {
        utils.failure(ctx, e);
    }
});

// get single queue
router.get(`${BASE_URL}/:id`, async (ctx) => {
    try {
        const queue = await queries.getQueueById(ctx.params.id);
        utils.success(ctx, queue);
    } catch (e) {
        utils.failure(ctx, e);
    }
});

router.get(`${BASE_URL}/:id/nextTicket`, async (ctx) => {
    try {
        const queues = await queries.nextTicket(ctx.params.id);
        require('../index').io.sockets.emit('update', queues);
        utils.success(ctx, queues);
    } catch (e) {
        utils.failure(ctx, e);
    }
});

router.get(`${BASE_URL}/:id/currentTicket`, async (ctx) => {
    try {
        const ticket = await queries.getCurrentTicket(ctx.params.id);
        utils.success(ctx, {ticketId: ticket.id, status: ticket.status, userLogin: ticket.login, userFirstName: ticket.firstName, userLastName: ticket.lastName });
    } catch (e) {
        utils.failure(ctx, e);
    }
});

router.get(`${BASE_URL}/myStatus/:userId`, async (ctx) => {
    try {
        const queue = await queries.getQueueById(ctx.params.userId);
        utils.success(ctx, queue);
    } catch (e) {
        utils.failure(ctx, e);
    }
});

