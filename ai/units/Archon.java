package team137.ai.units;

import static battlecode.common.RobotType.*;
import static battlecode.common.MapLocation.getAllMapLocationsWithinRadiusSq;

import battlecode.common.*;
import team137.ai.actions.Action;
import team137.ai.actions.MoveAction;
import team137.ai.actions.archon.ActivateAction;
import team137.ai.actions.priority.Priority;
import team137.ai.actions.priority.units.ArchonPrioritySet;

import java.util.Random;

public class Archon extends BaseUnit {

  private final ArchonPrioritySet priorityMap;
  private final Team team;
  private final Random rand;

  public Archon(RobotController rc) {
    super(rc);
    priorityMap = new ArchonPrioritySet();
    team = rc.getTeam();
    rand = new Random(rc.getID());
  }

  @Override
  public void update() {

    MapLocation curLoc = rc.getLocation();
    MapLocation[] tiles = getAllMapLocationsWithinRadiusSq(curLoc, ARCHON.sensorRadiusSquared);
    RobotInfo[] neighbors = rc.senseNearbyRobots();

    try {
      checkClearRubble(curLoc);
      checkParts(curLoc);
      checkWallAvoidance(curLoc, tiles);
      checkNeutrals(curLoc, neighbors);
      checkEvasion(curLoc, neighbors);

      // debug
      rc.setIndicatorString(0, priorityMap.toString(7));

      // act!
      priorityMap.fairAct(rc, rand);
    }
    catch (GameActionException e) {
      e.printStackTrace();
    }

    // update priorities
    priorityMap.update();
  }

  public void checkClearRubble(MapLocation curLoc) {
    double curRubble = rc.senseRubble(curLoc);

    // see if we are stuck!
    if(curRubble > 100) {
      // TODO: send distress signal
    }
  }

  public void checkParts(MapLocation curLoc) {
//    double curParts = rc.senseParts(curLoc);

    // max parts
//    MapLocation maxPartsLoc = curLoc;
//    Direction maxPartsDir = OMNI;
//    double maxParts = 0;
  }

  public void checkMinRubblePath(MapLocation curLoc) {

    // min rubble
//    MapLocation minAdjacentRubbleLoc = curLoc;
//    Direction minAdjacentRubbleDir = OMNI;
//    double minAdjacentRubble = Double.MAX_VALUE;
  }

  public void checkWallAvoidance(MapLocation curLoc, MapLocation[] tiles) {
    for(MapLocation loc : tiles) {
      if(! safeOnTheMap(loc)) {
        Direction dirFromLoc = curLoc.directionTo(loc).opposite();
        MoveAction action = MoveAction.fromDirection(dirFromLoc);
        priorityMap.addPriority(action, Priority.LOWEST_PRIORITY);
      }
    }
  }

  public void checkNeutrals(MapLocation curLoc, RobotInfo[] neighbors) {
    boolean hasNeutrals = false;
    for(RobotInfo robo : neighbors) {
      if(robo.team == Team.NEUTRAL) {
        Direction dirToLoc = curLoc.directionTo(robo.location);
        Action action;
        Priority priority;
        if(curLoc.isAdjacentTo(robo.location)) {
          action = ActivateAction.fromDirection(dirToLoc);
          priority = Priority.LEVEL8_PRIORITY;
          hasNeutrals = true;
        }
        else {
          action = MoveAction.fromDirection(dirToLoc);
          priority = Priority.DEFAULT_PRIORITY;
        }
        priorityMap.addPriority(action, priority);
      }
    }

    if(! hasNeutrals) {
      priorityMap.forbidActivate();
    }
  }

  public void checkEvasion(MapLocation curLoc, RobotInfo[] neighbors) {
  }
}
