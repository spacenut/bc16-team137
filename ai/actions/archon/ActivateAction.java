package team137.ai.actions.archon;

import battlecode.common.*;
import team137.ai.actions.BaseAction;

import java.util.HashMap;
import java.util.Map;

public class ActivateAction extends BaseAction {

  private static final Map<Direction, ActivateAction> actionMap;

  static {
    actionMap = new HashMap<>(8);
    actionMap.put(Direction.NORTH, new ActivateAction(Direction.NORTH, "+n"));
    actionMap.put(Direction.NORTH_EAST, new ActivateAction(Direction.NORTH_EAST, "+ne"));
    actionMap.put(Direction.EAST, new ActivateAction(Direction.EAST, "+e"));
    actionMap.put(Direction.SOUTH_EAST, new ActivateAction(Direction.SOUTH_EAST, "+se"));
    actionMap.put(Direction.SOUTH, new ActivateAction(Direction.SOUTH, "+s"));
    actionMap.put(Direction.SOUTH_WEST, new ActivateAction(Direction.SOUTH_WEST, "+sw"));
    actionMap.put(Direction.WEST, new ActivateAction(Direction.WEST, "+w"));
    actionMap.put(Direction.NORTH_WEST, new ActivateAction(Direction.NORTH_WEST, "+nw"));
  }

  private final Direction dir;

  private ActivateAction(Direction dir, String repr) {
    super(repr);
    this.dir = dir;
  }

  @Override
  public boolean act(RobotController rc) throws GameActionException {
    MapLocation loc = rc.getLocation().add(dir);
    RobotInfo robot = rc.senseRobotAtLocation(loc);

    if(robot != null && robot.team == Team.NEUTRAL) {
      rc.activate(loc);
      return true;
    }
    return false;
  }

  public static ActivateAction fromDirection(Direction dir) {
    return actionMap.get(dir);
  }
}
