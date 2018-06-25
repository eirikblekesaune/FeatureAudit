+ ControlSpec {
	random{
		^rrand(this.minval, this.maxval);
	}

	makeView{arg parent, action, settings;
		var result;
		var labelView, unitsView;
		var ctrlView;
		var valueLabel;
		ctrlView = NumberBox()
		.clipLo_(this.minval)
		.clipHi_(this.maxval)
		.toolTip_("min: % max: %".format(this.minval, this.maxval));
		ctrlView.action = action;

		//if it is an integer step
		if((this.step - this.step.asInt) == 0.0, {
			ctrlView.decimals = 0;
			ctrlView.scroll_step = 1;
			ctrlView.step = 1;
		}, {
			ctrlView.decimals = 3;
			ctrlView.scroll_step = 0.1;
			ctrlView.step = 0.1;
		});

		if(settings.notNil, {
			if(settings.includesKey(\label), {
				labelView = StaticText().string_(settings[\label]);
			}, {
				labelView = nil;
			});
			if(settings.includesKey(\type) and: {settings[\type] == \slider}, {
				valueLabel = ctrlView;
				ctrlView = Slider();
				ctrlView.action_({arg slid;
					var val;
					val = this.map(slid.value);
					action.value(val);
					
					{valueLabel.value_(val)}.defer;
				});
				valueLabel.value_(this.map(ctrlView.value));
				ctrlView.orientation = \horizontal;
				ctrlView.background = ctrlView.background.alpha_(0.0);
				ctrlView.thumbSize = 2;
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
				StackLayout(ctrlView, valueLabel).mode_(1),
				unitsView
			)
		).minSize_(Size(150, 30));

		^result;
	}
}
