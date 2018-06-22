OptionsSpec {
	var <options;
	var <default;

	*new{arg options, default;
		^super.newCopyArgs(options, default);
	}

	minval{ ^options.first; }
	maxval{ ^options.last; }
	random{ ^options.choose; }

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
