package winglessflight.common.tileentity;

import winglessflight.WinglessFlight;

public class BasicFlightTile extends FlightTileBase {

	public BasicFlightTile() {
		super(WinglessFlight.Config.radiusBsc, WinglessFlight.Config.chargeTimeBsc * 20);
	}
}
