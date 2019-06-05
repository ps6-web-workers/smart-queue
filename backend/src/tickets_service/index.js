const mqtt = require('mqtt');
const client  = mqtt.connect('mqtt://test.mosquitto.org');
const queries = require('./db/queries/queues');

client.on('connect', function () {
    client.subscribe('nextTicket');
    client.subscribe('queuesRequest');
    client.subscribe('currentTicketRequest');
});

client.on('message', async function (topic, message) {
    if (topic === 'nextTicket') {
        const queues = await queries.nextTicket(parseInt(message.toString()));
        client.publish('queuesUpdate', JSON.stringify(queues));
    } else if (topic === 'queuesFirstConnection') {
        const queues = await queries.getAllQueues();
        client.publish('queuesResponse', JSON.stringify(queues));
    } else if (topic === 'currentTicketRequest') {
        const currentTicket = await queries.getCurrentTicket(parseInt(message.toString()));
        client.publish('currentTicketResponse', JSON.stringify(currentTicket));
    }
    client.end();
});
