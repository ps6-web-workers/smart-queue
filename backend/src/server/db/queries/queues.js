const knex = require('../connection');

async function getAllQueues() {
    const queues = await knex('queues').select('*');
    for (let j = 0; j < queues.length; j++) {
        queues[j].users = [];
    }
    const usersToQueues = await knex.table('usersToQueues')
        .leftJoin('users', 'usersToQueues.userLogin', '=', 'users.login')
        .column({queueId: 'usersToQueues.id'}, 'queueName', 'login', 'firstName', 'lastName')
        .select('*');
    for (let i = 0; i < usersToQueues.length; i++) {
        for (let j = 0; j < queues.length; j++) {
            if (usersToQueues[i].queueName === queues[j].name) {
                queues[j].users.push({
                    id: usersToQueues[i].queueId,
                    login: usersToQueues[i].login,
                    firstName: usersToQueues[i].firstName,
                    lastName: usersToQueues[i].lastName
                });
            }
        }
    }
    return queues;
}

module.exports = {
    getAllQueues,
};
