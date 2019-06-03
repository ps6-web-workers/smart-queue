const path = require('path');

const BASE_PATH = path.join(__dirname, 'src', 'server', 'db');

module.exports = {
  // development environment
  development: {
    client: 'pg',
    connection: 'postgres://yursilv_test:test0079835@postgresql-yursilv.alwaysdata.net:5432/yursilv_ps6_final',
    migrations: {
      directory: path.join(BASE_PATH, 'migrations')
    },
    seeds: {
      directory: path.join(BASE_PATH, 'seeds')
    }
  },
  // test environment (using a separate database)
  test: {
    client: 'pg',
    connection: 'postgres://yursilv_test:test0079835@postgresql-yursilv.alwaysdata.net:5432/yursilv_ps6_final_test',
    migrations: {
      directory: path.join(BASE_PATH, 'migrations')
    },
    seeds: {
      directory: path.join(BASE_PATH, 'seeds')
    }
  },
};
