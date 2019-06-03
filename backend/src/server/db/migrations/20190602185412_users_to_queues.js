exports.up = (knex) => {
    return knex.schema.createTable('usersToQueues', (table) => {
        table.increments();
        table.string('queueName').notNullable();
        table.string('userLogin').notNullable();
        table.foreign('queueName').references('name').inTable('queues');
        table.foreign('userLogin').references('login').inTable('users');
    });
};

exports.down = (knex) => {
    return knex.schema.dropTable('usersToQueues');
};
