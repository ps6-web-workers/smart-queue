const knex = require('../connection');

async function getAllQueues() {
    const queues = await knex('queues').select('*');
    for (let j = 0; j < queues.length; j++) {
        queues[j].tickets = [];
    }
    const tickets = await knex.table('tickets')
        .select('*')
        .leftJoin('users', 'tickets.userId', '=', 'users.id')
        .leftJoin('queues', 'tickets.queueId', '=', 'queues.id')
        .column({ticketId: 'tickets.id'}, 'status', 'login', 'firstName', 'lastName');

    for (let i = 0; i < tickets.length; i++) {
        for (let j = 0; j < queues.length; j++) {
            if (tickets[i].queueId === queues[j].id) {
                queues[j].tickets.push({
                    ticketId: tickets[i].ticketId,
                    ticketStatus: tickets[i].status,
                    userLogin: tickets[i].login,
                    userFirstName: tickets[i].firstName,
                    userLastName: tickets[i].lastName
                });
            }
        }
    }
    return queues;
}

async function getQueueById(id) {
    const queues = await knex('queues')
        .select('*')
        .where({ id: +id });
    if (queues.length === 0) {
        throw new Error('Queue not found');
    }
    const queue = queues[0];
    queue.tickets = [];

    const tickets = await knex.table('tickets')
        .leftJoin('users', 'tickets.userId', '=', 'users.id')
        .leftJoin('queues', 'tickets.queueId', '=', 'queues.id')
        .column({ticketId: 'tickets.id'}, 'status', 'login', 'firstName', 'lastName')
        .select('*')
        .where({queueId: queue.id});

    for (let i = 0; i < tickets.length; i++) {
        queue.tickets.push({
            ticketId: tickets[i].ticketId,
            status: tickets[i].status,
            userLogin: tickets[i].login,
            userFirstName: tickets[i].firstName,
            userLastName: tickets[i].lastName
        });
    }
    return queue;
}

/*
async function nextTicket(id) {
    let nextTicket = {};

    const lastTwoTickets = await knex('tickets')
        .select('tickets.id', 'userLogin')
        .leftJoin('queues', 'tickets.queueName', '=', 'queues.name')
        .where('queues.id', id)
        .orderBy('tickets.id', 'desc')
        .limit(2);

    console.log(lastTwoTickets);

    if (lastTwoTickets.length === 0) {
        throw new Error('No tickets found');
    } else if (lastTwoTickets.length > 1) {
        nextTicket = await knex('users')
            .select('*')
            .where({ login: lastTwoTickets[1].userLogin});
        nextTicket.ticketId = lastTwoTickets[1].ticketId;
    }

    await knex('tickets')
        .where({ id: lastTwoTickets[0].id })
        .del();

    return nextTicket;
}
*/


async function getCurrentTicket(queueId) {
    const currentTicketId = await knex('currentTickets')
        .select('*')
        .where('queueId', queueId);
    if (!currentTicketId[0]) {
        throw new Error('No tickets available');
    }
    const currentTicket = await knex('tickets')
        .select('tickets.id', 'tickets.status', 'tickets.userId', 'tickets.queueId', 'users.login', 'users.firstName', 'users.lastName')
        .where('tickets.id', currentTicketId[0].ticketId)
        .leftJoin('users', 'tickets.userId', '=', 'users.id')
        .leftJoin('queues', 'tickets.queueId', '=', 'queues.id');
    return currentTicket[0];
}

async function setCurrentTicket(queueId, ticketId) {
    const currentTicket = await knex('currentTickets')
        .insert({ queueId: queueId, ticketId: ticketId });
}

async function getFirstActiveTicket(queueId) {
    const firstActiveTicket = await knex('tickets')
        .select('*')
        .where({queueId: queueId, status: 'active'})
        .orderBy('id', 'asc')
        .limit(1);
    if (!firstActiveTicket[0]) {
        throw new Error('No tickets available');
    }
    return firstActiveTicket[0];
}

async function setUserTicketsPassive(queueId, userId) {
    await knex('tickets')
        .where('userId', userId)
        .where('queueId', '!=', queueId)
        .update({
            status: 'passive',
        });
}

async function setUserTicketsActive(userId) {
    await knex('tickets')
        .where('userId', userId)
        .update({
            status: 'active',
        });
}

async function deleteCurrentTicket(ticketId) {
    await knex('tickets')
        .where({ id: ticketId })
        .del();
    await knex('currentTickets')
        .where({ ticketId: ticketId })
        .del();
}

async function nextTicket(queueId) {
    const currentTicket = await getCurrentTicket(queueId);
    await deleteCurrentTicket(currentTicket.id);
    await setUserTicketsActive(currentTicket.userId);

    const nextCurrentTicket = await getFirstActiveTicket(queueId);
    await setCurrentTicket(queueId, nextCurrentTicket.id);
    await setUserTicketsPassive(queueId, nextCurrentTicket.userId);

    return await getAllQueues();
}

module.exports = {
    getAllQueues,
    getQueueById,
    nextTicket,
    getCurrentTicket
};
