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


            // ALL TICKETS
            await knex('tickets').del();

            // BRI
            await knex('tickets').insert({
                status: 'current',
                queueId: 1,
                userId: 1
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 1,
                userId: 5
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 1,
                userId: 2
            });
            await knex('tickets').insert({
                status: 'passive',
                queueId: 1,
                userId: 3
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 1,
                userId: 7
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 1,
                userId: 9
            });
            await knex('tickets').insert({
                status: 'passive',
                queueId: 1,
                userId: 8
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 1,
                userId: 10
            });

            // Responsable de stage
            await knex('tickets').insert({
                status: 'current',
                queueId: 2,
                userId: 3
            });
            await knex('tickets').insert({
                status: 'passive',
                queueId: 2,
                userId: 1
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 2,
                userId: 4
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 2,
                userId: 6
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 2,
                userId: 7
            });

            // Tuteur de stage
            await knex('tickets').insert({
                status: 'current',
                queueId: 3,
                userId: 8
            });
            await knex('tickets').insert({
                status: 'passive',
                queueId: 3,
                userId: 1
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 3,
                userId: 9
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 3,
                userId: 10
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 3,
                userId: 11
            });
            await knex('tickets').insert({
                status: 'active',
                queueId: 3,
                userId: 12
            });

            resolve();
        } catch (e) {
            console.log(e);
            reject();
        }
    });
};
