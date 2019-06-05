process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();
const chaiHttp = require('chai-http');
chai.use(chaiHttp);

const index = require('../src/tickets_service/index');

// simple test just to verify if the tickets_service is running
describe('routes : index', () => {
    describe('GET /', () => {
        it('should return json', (done) => {
            chai.request(index.server)
                .get('/')
                .end((err, res) => {
                    should.not.exist(err);
                    res.status.should.eql(200);
                    res.type.should.eql('application/json');
                    res.body.status.should.equal('success');
                    res.body.message.should.eql('tickets_service running');
                    done();
                });
        });
    });

});
