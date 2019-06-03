process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();
const chaiHttp = require('chai-http');
chai.use(chaiHttp);

const server = require('../src/server/index');

// simple test just to verify if the server is running
describe('routes : index', () => {
    describe('GET /', () => {
        it('should return json', (done) => {
            chai.request(server)
                .get('/')
                .end((err, res) => {
                    should.not.exist(err);
                    res.status.should.eql(200);
                    res.type.should.eql('application/json');
                    res.body.status.should.equal('success');
                    res.body.message.should.eql('server running');
                    done();
                });
        });
    });

});
