exports.up = (knex) => {
    return knex.schema.createTable('tickets', (table) => {
        table.increments();
        table.string('status').notNullable();
        table.integer('queueId').notNullable();
        table.integer('userId').notNullable();
        table.foreign('queueId').references('id').inTable('queues');
        table.foreign('userId').references('id').inTable('users');
    });
};

exports.down = (knex) => {
    return knex.schema.dropTable('tickets');
};
