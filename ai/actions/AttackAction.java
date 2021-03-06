package team137.ai.actions;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class AttackAction extends BaseAction {

  private MapLocation loc;

  protected AttackAction(MapLocation loc) {
    super("!" + loc);
    this.loc = loc;
  }

  @Override
  public boolean act(RobotController rc) throws GameActionException {
    if(rc.canAttackLocation(loc)) {
      rc.attackLocation(loc);
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return loc.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof AttackAction && loc.equals(((AttackAction) o).loc);
  }

  public static AttackAction target(MapLocation loc) {
    return new AttackAction(loc);
  }
}
