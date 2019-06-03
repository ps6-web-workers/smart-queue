process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();
const chaiHttp = require('chai-http');
chai.use(chaiHttp);

const server = require('../src/server/index');
const knex = require('../src/server/db/connection');

describe('routes : users', () => {

    // the migrations are applied to the test database
    beforeEach(() => {
        return knex.migrate.rollback()
            .then(() => { return knex.migrate.latest(); })
            .then(() => { return knex.seed.run();});
    });

    // the test database is rolled back to the initial state
    afterEach(() => {
        return knex.migrate.rollback();
    });

    describe('GET /api/queues', () => {
        it('should return all queues', (done) => {
            chai.request(server)
                .get('/api/queues')
                .end((err, res) => {
                    should.not.exist(err);
                    res.status.should.equal(200);
                    res.type.should.equal('application/json');
                    res.body.status.should.eql('success');
                    res.body.data.queues.length.should.eql(3);
                    res.body.data.queues[0].users[0].should.include.keys(
                        'id', 'login', 'firstName', 'lastName'
                    );
                    done();
                });
        });
    });
});
