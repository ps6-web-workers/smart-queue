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
            await knex('usersToQueues').del();

            // BRI
            await knex('usersToQueues').insert({
                queueName: 'BRI',
                userLogin: 'romain'
            });
            await knex('usersToQueues').insert({
                queueName: 'BRI',
                userLogin: 'anass'
            });
            await knex('usersToQueues').insert({
                queueName: 'BRI',
                userLogin: 'yury'
            });

            // Responsable de stage
            await knex('usersToQueues').insert({
                queueName: 'Responsable de stage',
                userLogin: 'romain'
            });
            await knex('usersToQueues').insert({
                queueName: 'Responsable de stage',
                userLogin: 'nouamane'
            });
            await knex('usersToQueues').insert({
                queueName: 'Responsable de stage',
                userLogin: 'paul-marie'
            });
            await knex('usersToQueues').insert({
                queueName: 'Responsable de stage',
                userLogin: 'donelia'
            });

            // Tuteur de stage
            await knex('usersToQueues').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'yury'
            });
            await knex('usersToQueues').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'nouamane'
            });
            await knex('usersToQueues').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'romain'
            });
            await knex('usersToQueues').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'donelia'
            });
            await knex('usersToQueues').insert({
                queueName: 'Tuteur de stage',
                userLogin: 'anass'
            });

            resolve();
        } catch (e) {
            console.log(e);
            reject();
        }
    });
};
