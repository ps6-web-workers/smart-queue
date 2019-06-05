exports.up = (knex) => {
    return knex.schema.createTable('users', (table) => {
        table.increments();
        table.string('login').notNullable().unique();
        table.string('firstName').notNullable();
        table.string('lastName').notNullable();
    });
};

exports.down = (knex) => {
    return knex.schema.dropTable('users');
};
