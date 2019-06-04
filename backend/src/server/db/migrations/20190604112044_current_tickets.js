exports.up = (knex) => {
    return knex.schema.createTable('currentTickets', (table) => {
        table.integer('ticketId');
        table.integer('queueId');
        table.primary(['ticketId', 'queueId']);
    });
};

exports.down = (knex) => {
    return knex.schema.dropTable('currentTickets');
};
