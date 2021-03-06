package team137.ai.units;

import battlecode.common.*;
import team137.ai.actions.Action;
import team137.ai.actions.AttackAction;
import team137.ai.actions.ClearAction;
import team137.ai.actions.MoveAction;
import team137.ai.actions.priority.Priority;
import team137.ai.actions.priority.units.SoldierPrioritySet;
import team137.ai.tables.Directions;
import team137.ai.tables.robots.FleeWeights;
import team137.ai.tables.robots.RobotWeights;

import java.util.Random;

import static battlecode.common.RobotType.*;

public class Soldier extends MovableUnit {

  private static final FleeWeights FLEE_TABLE = FleeWeights.newInstance();
  private static final RobotWeights ATTACK_TABLE = RobotWeights.newInstance();

  static {
    ATTACK_TABLE.put(ZOMBIEDEN, Priority.LEVEL8_PRIORITY.value);
    ATTACK_TABLE.put(BIGZOMBIE, Priority.LEVEL2_PRIORITY.value);
  }

  private final SoldierPrioritySet prioritySet;
  private final Random rand;
  private final Team team;

  public Soldier(RobotController rc) {
    super(rc);
    prioritySet = new SoldierPrioritySet();
    rand = new Random(rc.getID());
    team = rc.getTeam();
  }

  @Override
  public void update() {

    /// TURN VARIABLES

    MapLocation curLoc = rc.getLocation();
    RobotInfo[] localEnemies = rc.senseNearbyRobots(SOLDIER.sensorRadiusSquared);
    MapLocation attackLoc = AttackUnits.findTarget(
        localEnemies,
        ATTACK_TABLE,
        curLoc,
        Priority.LEVEL2_PRIORITY,
        team);

    /// START TURN

    try {

      if(rc.isCoreReady() || rc.isWeaponReady()) {

        rc.setIndicatorString(0, prioritySet.toString());
        rc.setIndicatorString(1, "" + attackLoc);

        checkAttack(attackLoc, curLoc);

        RobotInfo[] localFriends = rc.senseNearbyRobots(2, team);
        if (localFriends.length > 3) {
          StringBuilder sb = new StringBuilder();
          for (Direction dir : Directions.fairCardinals(rand)) {
            prioritySet.putPriority(MoveAction.inDirection(dir), Priority.DEFAULT_PRIORITY);
            sb.append(dir);
            sb.append(' ');
          }
          rc.setIndicatorString(2, sb.toString());
        }
        if (rc.isCoreReady() && rc.isWeaponReady()) {
          Action action = prioritySet.fairAct(rc, rand);
          decay(action);
        }
      }

    }
    catch (GameActionException e) {
      e.printStackTrace();
    }
  }

  private void decay(Action action) {
    if(action instanceof MoveAction) {
      prioritySet.putPriority(action, Priority.FORBID_PRIORITY);
    }
    else if(action instanceof AttackAction) {
      prioritySet.putPriority(action, Priority.FORBID_PRIORITY);
    }
  }

  private void checkAttack(MapLocation attackLoc, MapLocation curLoc) {
    if(attackLoc == null) {
      return;
    }

    if(rc.canAttackLocation(attackLoc)) {
      prioritySet.putPriority(AttackAction.target(attackLoc), Priority.LEVEL4_PRIORITY);
    }
    else {

      Signal signal;
      while ((signal = rc.readSignal()) != null) {
        if (signal.getTeam() == team) {
          break;
        }
      }

      Direction dirToLoc;

      if (signal != null && signal.getTeam() == team) {
        MapLocation loc = signal.getLocation();
        dirToLoc = curLoc.directionTo(loc);
      } else {
        dirToLoc = curLoc.directionTo(attackLoc);
      }
      prioritySet.putPriority(ClearAction.inDirection(dirToLoc), Priority.LEVEL4_PRIORITY);
      prioritySet.putPriority(MoveAction.inDirection(dirToLoc), Priority.LEVEL4_PRIORITY);
      prioritySet.putPriority(AttackAction.target(attackLoc), Priority.FORBID_PRIORITY);
    }
  }
}