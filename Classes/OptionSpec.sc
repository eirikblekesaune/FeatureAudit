OptionsSpec {
	var <options;
	var <default;

	*new{arg options, default;
		^super.newCopyArgs(options, default);
	}

	map{arg val;
		var result;
		if(val.isNumber, {
			result = options[val];
		}, {
			if(options.includes(val), {
				result = val;
			});
		});
		^result;
	}

	unmap{arg symbol;
		^options.indexOf(symbol);
	}
}
