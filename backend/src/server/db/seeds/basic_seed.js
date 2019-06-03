exports.seed = (knex) => {
    return new Promise(async (resolve, reject) => {
        try {
            // USERS
            await knex('users').del();

            await knex('users').insert({
                login: 'romain',
                firstName: 'Romain',
                lastName: 'Giuntini',
            });
            await knex('users').insert({
                login: 'yury',
                firstName: 'Yury',
                lastName: 'Silvestrov-Henocq',
            });
            await knex('users').insert({
                login: 'nouamane',
                firstName: 'Nouamane',
                lastName: 'Azzouzi',
            });
            await knex('users').insert({
                login: 'paul-marie',
                firstName: 'Paul-Marie',
                lastName: 'Djekinnou',
            });
            await knex('users').insert({
                login: 'anass',
                firstName: 'Anass',
                lastName: 'Sabri',
            });
            await knex('users').insert({
                login: 'donelia',
                firstName: 'Donélia',
                lastName: 'Monin',
            });
            await knex('users').insert({
                login: 'vincentc',
                firstName: 'Vincent',
                lastName: 'Coppe',
            });
            await knex('users').insert({
                login: 'armand',
                firstName: 'Armand',
                lastName: 'Boulanger',
            });
            await knex('users').insert({
                login: 'alexn',
                firstName: 'Alexandre',
                lastName: 'Nicaise',
            });
            await knex('users').insert({
                login: 'kevinv',
                firstName: 'Kevin',
                lastName: 'Valerio',
            });
            await knex('users').insert({
                login: 'yannis',
                firstName: 'Yannis',
                lastName: 'Falco',
            });
            await knex('users').insert({
                login: 'gabriel',
                firstName: 'Gabriel',
                lastName: 'Revelli',
            });


            // QUEUES
            await knex('queues').del();

            await knex('queues').insert({
                name: 'BRI',
            });
            await knex('queues').insert({
                name: 'Responsable de stage',
            });
            await knex('queues').insert({
                name: 'Tuteur de stage',
            });


            // USERS TO QUEUES
            await knex('tickets').del();

            // BRI
            await knex('tickets').insert({
                queueName: 'BRI',
                userLogin: 'romain'
            });
            await knex('tickets').insert({
                queueName: 'BRI',
                userLogin: 'anass'
            });
            await knex('tickets').insert({
                queueName: 'BRI',
                userLogin: 'yury'
            });

            // Responsable de stage
            await knex('tickets').insert({
                queueName: 'Responsable de stage',
                userLogin: 'nouamane'
            });
            await knex('tickets').insert({
                queueName: 'Responsable de stage',
                userLogin: 'paul-marie'
            });
            await knex('tickets').insert({
                queueName: 'Responsable de stage',
                userLogin: 'donelia'
            });
            await knex('tickets').insert({
                queueName: 'Responsable de stage',
                userLogin: 'vincentc'
            });

            // Tuteur de stage
            await knex('tickets').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'armand'
            });
            await knex('tickets').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'alexn'
            });
            await knex('tickets').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'kevinv'
            });
            await knex('tickets').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'yannis'
            });
            await knex('tickets').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'gabriel'
            });

            resolve();
        } catch (e) {
            console.log(e);
            reject();
        }
    });
};
