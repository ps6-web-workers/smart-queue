exports.up = (knex) => {
    return knex.schema.createTable('queues', (table) => {
        table.increments();
        table.string('name').notNullable().unique();
    });
};

exports.down = (knex) => {
    return knex.schema.dropTable('queues');
};
