var exec = require('cordova/exec');

exports.openGpio = function (name, success, error) {
    exec(success, error, 'gpio', 'openGpio', [name]);
};

exports.close = function (name, success, error) {
	exec(success, error, 'gpio', 'close', [name]);
};

exports.setValue = function (name, value, success, error) {
	exec(success, error, 'gpio', 'setValue', [name, value]);
};

exports.getValue = function (name, success, error) {
	exec(success, error, 'gpio', 'setValue', [name]);
};
