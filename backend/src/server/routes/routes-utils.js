// sets the params of the response when the request was successfully completed
function success(ctx, data) {
    ctx.status = 200;
    ctx.body = data;
}

// sets the params of the response when the request failed to complete
function failure(ctx, e) {
    let status = 500;
    let error = new Error('Sorry, an error has occurred');
    if (e) {
        error = e;
        if (e.message === 'Queue not found') {
            status = 404;
        }
    }
    ctx.status = status;
    ctx.body = {
        status: 'error',
        message: error.message,
    };
}

module.exports = {
    success,
    failure
};
