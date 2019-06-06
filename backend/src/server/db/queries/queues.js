const knex = require('../connection');

async function getAllQueuesNames() {
    return await knex('queues')
        .select('*');
}

async function getAllQueues() {
    const queues = await knex('queues').select('*');
    for (let j = 0; j < queues.length; j++) {
        queues[j].tickets = [];
    }
    const tickets = await knex.table('tickets')
        .select('*')
        .leftJoin('users', 'tickets.userId', '=', 'users.id')
        .leftJoin('queues', 'tickets.queueId', '=', 'queues.id')
        .column({ticketId: 'tickets.id'}, 'status', 'login', 'firstName', 'lastName')
        .orderBy('ticketId', 'asc');

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
        .where({queueId: queue.id})
        .orderBy('ticketId', 'asc');

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


async function getCurrentTicket(queueId) {
    const currentTicket = await knex('tickets')
        .select('*')
        .where('queueId', queueId)
        .where('status', 'current');
    if (currentTicket.length === 0) {
        throw new Error('No tickets available');
    }
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
    if (firstActiveTicket.length === 0) {
        throw new Error('No tickets available');
    }
    return firstActiveTicket[0];
}

async function setUserTicketsPassive(userId) {
    await knex('tickets')
        .where('userId', userId)
        .where('status', 'active')
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

async function updateTickets() {
    const queues = await knex('queues')
        .select('*');
    for (let i=0; i < queues.length; i++) {
        try {
            await getCurrentTicket(queues[i].id);
        } catch (e) {
            if (e.message === 'No tickets available') {
                try {
                    const ticket = await getFirstActiveTicket(queues[i].id);
                    await knex('tickets')
                        .where('id', ticket.id)
                        .update({
                            status: 'current',
                        });
                    await setUserTicketsPassive(ticket.userId);
                } catch (e) {}
            } else {
                throw e;
            }
        }
    }
}

async function deleteCurrentTicket(ticketId) {
    await knex('tickets')
        .where({ id: ticketId })
        .del();
}

async function nextTicket(queueId) {
    try {
        const currentTicket = await getCurrentTicket(queueId);
        await deleteCurrentTicket(currentTicket.id);
        await setUserTicketsActive(currentTicket.userId);
        await updateTickets();

        await getCurrentTicket(queueId);
    } catch (e) {
        if (e.message === 'No tickets available') {
            return await getAllQueues();
        } else {
            throw e;
        }
    }
    return await getAllQueues();
}


async function addTicket(ticket) {
    let status = 'active';
    const alreadyExistingTickets = await knex('tickets')
        .select('*')
        .where('userId', ticket.userId);
    // on vérifie si l'utilisateur a déjà un ticket "current"
    for (let i = 0; i < alreadyExistingTickets.length; i++) {
        if (alreadyExistingTickets[i].status === 'current') {
            status = 'passive';
            break;
        }
    }

    const newTicket = await knex('tickets')
        .insert({ status: status, queueId: ticket.queueId, userId: ticket.userId })
        .returning('*');
    await updateTickets();
    const newTicketUpdated = await knex('tickets')
        .select('*')
        .where({ id: newTicket[0].id });
    return newTicketUpdated[0];
}

async function removeTicket(ticket) {
    const removedTicket = await knex('tickets')
        .where({ queueId: ticket.queueId, userId: ticket.userId })
        .del()
        .returning('*');
    if (removedTicket.length === 0) {
        throw new Error('No tickets available');
    }
    await updateTickets();
    return removedTicket[0];
}


async function getFullTicketBody(infos) {
    let where1;
    let where2;

    if (infos.userId) {
        where1 = `users.id = ${infos.userId}`;
    } else if (infos.userLogin) {
        where1 = `users.login = '${infos.userLogin}'`;
    } else if (infos.login) {
        where1 = `users.login = '${infos.login}'`;
    } else if (infos.ticket) {
        if (infos.ticket.userId) {
            where1 = `users.id = ${infos.ticket.userId}`;
        } else if (infos.ticket.userLogin) {
            where1 = `users.login = '${infos.ticket.userLogin}'`;
        } else if (infos.ticket.login) {
            where1 = `users.login = '${infos.ticket.login}'`;
        } else {
            throw new Error('Incorrect payload');
        }
    } else {
        throw new Error('Incorrect payload');
    }

    if (infos.queueId) {
        where2 = `queues.id = ${infos.queueId}`;
    } else if (infos.queueName) {
        where2 = `queues.name = '${infos.queueName}'`;
    } else if (infos.ticket) {
        if (infos.ticket.queueId) {
            where2 = `queues.id = ${infos.ticket.queueId}`;
        } else if (infos.ticket.queueName) {
            where2 = `queues.name = '${infos.ticket.queueName}'`;
        } else {
            throw new Error('Incorrect payload');
        }
    } else {
        throw new Error('Incorrect payload');
    }

    const ticket = await knex
        .select({ userLogin: 'login', userFirstName: 'firstName', userLastName: 'lastName', queueId: 'queues.id', queueName: 'queues.name', userId: 'users.id'})
        .from(knex.raw('users, queues'))
        .whereRaw(where1)
        .whereRaw(where2);

    return ticket[0];
}

async function getDisplayTicket(ticket) {
    const fullTicket = await getFullTicketBody(ticket);
    fullTicket.ticketStatus = ticket.status;
    fullTicket.ticketId = ticket.id;
    delete fullTicket.queueId;
    delete fullTicket.queueName;
    delete fullTicket.userId;
    return fullTicket;
}

async function checkIfTicketAlreadyExists(ticket) {
    const t = await knex('tickets')
        .select('*')
        .where({ userId: ticket.userId, queueId: ticket.queueId });
    if (t.length !== 0) {
        throw new Error('This user already has a ticket in this queue');
    }
}

module.exports = {
    getAllQueues,
    getQueueById,
    nextTicket,
    getCurrentTicket,
    getFullTicketBody,
    addTicket,
    getAllQueuesNames,
    getDisplayTicket,
    checkIfTicketAlreadyExists,
    removeTicket
};
