AuditSection {
	var <startIndex;
	var <endIndex;

	*new{arg startIndex, endIndex;
		^super.newCopyArgs(startIndex, endIndex);
	}

	printOn{arg stream;
		stream << "AuditSection(%, %)".format(startIndex, endIndex);
	}

	< { arg aSection;
		if(aSection.class == this.class, {
			^startIndex < aSection.startIndex;
		}, {
			BinaryOpFailureError(this, '<', aSection).throw;
		});
	}

	> { arg aSection;
		if(aSection.class == this.class, {
			^startIndex > aSection.startIndex;
		}, {
			BinaryOpFailureError(this, '>', aSection).throw;
		});
	}

	<= { arg aSection;
		if(aSection.class == this.class, {
			^startIndex <= aSection.startIndex;
		}, {
			BinaryOpFailureError(this, '<=', aSection).throw;
		});
	}

	>= { arg aSection;
		if(aSection.class == this.class, {
			^startIndex >= aSection.startIndex;
		}, {
			BinaryOpFailureError(this, '>=', aSection).throw;
		});
	}

	== { arg aSection;
		if(aSection.class == this.class, {
			^startIndex == aSection.startIndex;
		}, {
			BinaryOpFailureError(this, '==', aSection).throw;
		});
	}

	!= { arg aSection;
		if(aSection.class == this.class, {
			^(this == aSection).not;
		}, {
			BinaryOpFailureError(this, '!=', aSection).throw;
		});
	}

	hash {
		^this.instVarHash([\startIndex, \endIndex]);
	}

}
