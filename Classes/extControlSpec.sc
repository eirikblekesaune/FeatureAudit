+ ControlSpec {
	random{
		^rrand(this.minval, this.maxval);
	}

	makeView{arg parent, action, settings;
		var result;
		var labelView, unitsView;
		var numbox = NumberBox()
		.clipLo_(this.minval)
		.clipHi_(this.maxval)
		.toolTip_("min: % max: %".format(this.minval, this.maxval));
		//if it is an integer step
		if((this.step - this.step.asInt) > 0.0, {
			numbox.decimals = 0;
			numbox.scroll_step = 1;
			numbox.step = 1;
		}, {
			numbox.decimals = 3;
			numbox.scroll_step = 0.1;
			numbox.step = 0.1;
		});
		if(settings.includesKey(\label), {
			labelView = StaticText().string_(settings[\label]);
		}, {labelView = nil;});

		if(this.units.notNil, {
			unitsView = StaticText().string_(this.units)
		}, {unitsView = nil;});

		result = View(parent).layout_(
			HLayout(
				labelView,
				numbox,
				unitsView
			)
		).minSize_(Size(150, 30));
		^result;
	}
}
