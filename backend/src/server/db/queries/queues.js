const knex = require('../connection');

async function getAllQueues() {
    const queues = await knex('queues').select('*');
    for (let j = 0; j < queues.length; j++) {
        queues[j].tickets = [];
    }
    const tickets = await knex.table('tickets')
        .leftJoin('users', 'tickets.userLogin', '=', 'users.login')
        .column({ticketId: 'tickets.id'}, 'queueName', 'login', 'firstName', 'lastName')
        .select('*');
    for (let i = 0; i < tickets.length; i++) {
        for (let j = 0; j < queues.length; j++) {
            if (tickets[i].queueName === queues[j].name) {
                queues[j].tickets.push({
                    ticketId: tickets[i].ticketId,
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
        .select('*')
        .where({queueName: queue.name})
        .leftJoin('users', 'tickets.userLogin', '=', 'users.login')
        .column({ticketId: 'tickets.id'}, 'queueName', 'login', 'firstName', 'lastName')
        .select('*');

    for (let i = 0; i < tickets.length; i++) {
        queue.tickets.push({
            ticketId: tickets[i].ticketId,
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

async function nextTicket(queueId) {
    const firstTicket = await knex('tickets')
        .select('tickets.id')
        .leftJoin('queues', 'tickets.queueName', '=', 'queues.name')
        .where('queues.id', queueId)
        .orderBy('tickets.id', 'asc')
        .limit(1);

    if (firstTicket.length === 0) {
        throw new Error('No tickets left');
    }

    await knex('tickets')
        .where({ id: firstTicket[0].id })
        .del();

    return await getAllQueues();
}

async function currentTicket(queueId) {
    const firstTicket = await knex('tickets')
        .select({ ticketId: 'tickets.id' }, { userLogin: 'users.login'}, { userFirstName: 'users.firstName'}, { userLastName: 'users.lastName'})
        .leftJoin('queues', 'tickets.queueName', '=', 'queues.name')
        .leftJoin('users', 'tickets.userLogin', '=', 'users.login')
        .where('queues.id', queueId)
        .orderBy('tickets.id', 'asc')
        .limit(1);

    if (firstTicket.length === 0) {
        throw new Error('No tickets found');
    }

    return firstTicket[0];
}

module.exports = {
    getAllQueues,
    getQueueById,
    nextTicket,
    currentTicket
};
