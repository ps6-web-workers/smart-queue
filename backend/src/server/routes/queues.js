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
        utils.success(ctx, queues);
    } catch (e) {
        utils.failure(ctx, e);
    }
});

router.get(`${BASE_URL}/:id/currentTicket`, async (ctx) => {
    try {
        let ticket = await queries.getCurrentTicket(ctx.params.id);
        ticket = await queries.getDisplayTicket(ticket);
        utils.success(ctx, ticket);
    } catch (e) {
        utils.failure(ctx, e);
    }
});

router.post(`${BASE_URL}/:id/addTicket`, async (ctx) => {
    try {
        ctx.request.body.queueId = ctx.params.id;
        let newFullTicketBody = await queries.getFullTicketBody(ctx.request.body);

        await queries.checkIfTicketAlreadyExists(newFullTicketBody);

        const newTicket = await queries.addTicket(newFullTicketBody);
        const displayTicket = await queries.getDisplayTicket(newTicket);
        utils.success(ctx, displayTicket);
    } catch (e) {
        utils.failure(ctx, e);
    }
});

router.post(`${BASE_URL}/:id/removeTicket`, async (ctx) => {
    try {
        ctx.request.body.queueId = ctx.params.id;
        let newFullTicketBody = await queries.getFullTicketBody(ctx.request.body);

        const newTicket = await queries.removeTicket(newFullTicketBody);
        const displayTicket = await queries.getDisplayTicket(newTicket);
        utils.success(ctx, displayTicket);
    } catch (e) {
        utils.failure(ctx, e);
    }
});

router.get(`/api/queuesNames`, async (ctx) => {
    try {
        const queuesNames = await queries.getAllQueuesNames();
        utils.success(ctx, queuesNames);
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

