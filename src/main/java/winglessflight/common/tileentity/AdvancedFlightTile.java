package winglessflight.common.tileentity;

import winglessflight.WinglessFlight;

public class AdvancedFlightTile extends FlightTileBase {

	public AdvancedFlightTile() {
		super(WinglessFlight.Config.radiusAdv, WinglessFlight.Config.chargeTimeAdv * 20);
	}
}
