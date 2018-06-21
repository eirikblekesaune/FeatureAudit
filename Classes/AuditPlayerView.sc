AuditPlayerView : View {
	var player;

	var window;
	var toolbarView;
	var featureDataDisplayView;
	var filenameLabel; //displays the path of the current filename

	var skipJack;
	var scroller, scrollerPressed = false;
	var featuresView;
	var featureViews;
	var criterionsView;
	var criterionViews;

	var soundFileControllView;
	var controlsView;
	var criterionViews;

	var selectedCriterion;

	var startSeconds;
	var endSeconds;


	player_{arg plr;
		player = plr;
	}

	refresh{

	}

	setViewRange{arg startSecs, endSecs;
		startSeconds = startSecs;
		endSeconds = endSecs;
		{
			scroller.lo = startSeconds / player.duration;
			scroller.hi = endSeconds / player.duration;
		}.defer;

		//set view range for feature views
//		gFeatureControllers.keysValuesDo({arg key, obj;
//			obj.setViewRange(startSeconds, endSeconds);
//		});


		//set view range for sound file view
//		if(gSoundFileController.notNil, {
//			gSoundFileController.setViewRange(startSeconds, endSeconds);
//		});
	}
}

AuditFeatureView : View {

}

