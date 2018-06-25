OptionsSpec {
	var <options;
	var <default;
	var <units;

	*new{arg options, default, units;
		^super.newCopyArgs(options, default, units);
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

	makeView{arg parent, action, settings;
		var result;
		var labelView, unitsView;
		var ctrlView;

		ctrlView = PopUpMenu()
		.items_(this.options)
		.action_({|menu| action.value(menu.item, menu.value)});

		if(settings.notNil, {
			if(settings.includesKey(\label), {
				labelView = StaticText().string_(settings[\label]);
			}, {
				labelView = nil;
			});
		});

		if(this.units.notNil, {
			unitsView = StaticText().string_(this.units)
		}, {
			unitsView = nil;
		});

		result = View(parent).layout_(
			HLayout(
				labelView,
				ctrlView,
				unitsView
			)
		).minSize_(Size(150, 30));

		^result;
	}
}
