package scenarios
import tcof.{Component, _}
import java.time._
import java.time.format._
import java.util.Collection
import java.util.ArrayList
import java.util.HashMap

class RunningExample(val now: LocalTime) extends Model {
class Subject(val subjectName: String, val location: String = null, val role: String = null, val shiftName: String = null) extends Component {
name(s"Subject $subjectName")
}


class Resource(val resourceName: String, val accessSubject: Subject) extends Component {
name(s"Resource $resourceName")
}


class System extends RootEnsemble {
object accessFactory extends Ensemble {
val allowedResources = components.select[Resource].filter(x => ((x.accessSubject != null && ((x.accessSubject.location != null && x.accessSubject.location.matches("(\\QFactory\\E)")) && (x.accessSubject.role != null && x.accessSubject.role.matches("(\\QWorker\\E)")) && (x.accessSubject.shiftName == "Early-Production") && (((now isBefore (LocalTime.parse("14:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME))) || (now equals (LocalTime.parse("14:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME)))) && ((now isAfter (LocalTime.parse("06:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME))) || (now equals (LocalTime.parse("06:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME))))))) || (x.accessSubject != null && ((x.accessSubject.location != null && x.accessSubject.location.matches("(\\QFactory\\E)")) && (x.accessSubject.role != null && x.accessSubject.role.matches("(\\QWorker\\E)"))) && shiftBeginMinus30mCheck(x)))).map[Resource](x => x.getClass().cast(x))
val allowedSubjects = components.select[Subject].filter(x => (((x.location != null && x.location.matches("(\\QFactory\\E)")) && (x.role != null && x.role.matches("(\\QWorker\\E)")) && (x.shiftName == "Early-Production") && (((now isBefore (LocalTime.parse("14:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME))) || (now equals (LocalTime.parse("14:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME)))) && ((now isAfter (LocalTime.parse("06:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME))) || (now equals (LocalTime.parse("06:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME)))))) || ((x.location != null && x.location.matches("(\\QFactory\\E)")) && (x.role != null && x.role.matches("(\\QWorker\\E)")) && shiftBeginMinus30mCheck(x)))).map[Subject](x => x.getClass().cast(x))

allow(allowedSubjects, "accessFactory", allowedResources)
}

object checkTimeToWorkingPlace extends Ensemble {
val allowedResources = components.select[Resource].filter(x => ((x.accessSubject != null && ((x.accessSubject.role != null && x.accessSubject.role.matches("(\\QForeman\\E)")))))).map[Resource](x => x.getClass().cast(x))
val allowedSubjects = components.select[Subject].filter(x => ((checkLocationOfWorker() && (x.role != null && x.role.matches("(\\QForeman\\E)"))))).map[Subject](x => x.getClass().cast(x))

allow(allowedSubjects, "checkTimeToWorkingPlace", allowedResources)
}

object giveOutEquipment extends Ensemble {
val allowedResources = components.select[Resource].filter(x => ((x.accessSubject != null && ((x.accessSubject.role != null && x.accessSubject.role.matches("(\\QWorker\\E)")))))).map[Resource](x => x.getClass().cast(x))
val allowedSubjects = components.select[Subject].filter(x => (((x.role != null && x.role.matches("(\\QWorker\\E)"))))).map[Subject](x => x.getClass().cast(x))

allow(allowedSubjects, "giveOutEquipment", allowedResources)
}

object enterWorkingPlace extends Ensemble {
val allowedResources = components.select[Resource].filter(x => ((x.accessSubject != null && ((x.accessSubject.location != null && x.accessSubject.location.matches("(\\QProduction_Line\\E)")) && (x.accessSubject.role != null && x.accessSubject.role.matches("(\\QWorker\\E)")))))).map[Resource](x => x.getClass().cast(x))
val allowedSubjects = components.select[Subject].filter(x => (((x.location != null && x.location.matches("(\\QProduction_Line\\E)")) && (x.role != null && x.role.matches("(\\QWorker\\E)"))))).map[Subject](x => x.getClass().cast(x))

allow(allowedSubjects, "enterWorkingPlace", allowedResources)
}

object leaveWorkingPlace extends Ensemble {
val allowedResources = components.select[Resource].filter(x => ((x.accessSubject != null && ((x.accessSubject.location != null && x.accessSubject.location.matches("(\\QProduction_Line\\E)")) && (x.accessSubject.role != null && x.accessSubject.role.matches("(\\QWorker\\E)"))) && shiftEndPlus10mCheck(x)))).map[Resource](x => x.getClass().cast(x))
val allowedSubjects = components.select[Subject].filter(x => (((x.location != null && x.location.matches("(\\QProduction_Line\\E)")) && (x.role != null && x.role.matches("(\\QWorker\\E)")) && shiftEndPlus10mCheck(x)))).map[Subject](x => x.getClass().cast(x))

allow(allowedSubjects, "leaveWorkingPlace", allowedResources)
}

object leaveFactory extends Ensemble {
val allowedResources = components.select[Resource].filter(x => ((x.accessSubject != null && ((x.accessSubject.location != null && x.accessSubject.location.matches("(\\QFactory\\E)")) && (x.accessSubject.role != null && x.accessSubject.role.matches("(\\QWorker\\E)"))) && shiftEndPlus30mCheck(x)))).map[Resource](x => x.getClass().cast(x))
val allowedSubjects = components.select[Subject].filter(x => (((x.location != null && x.location.matches("(\\QFactory\\E)")) && (x.role != null && x.role.matches("(\\QWorker\\E)")) && shiftEndPlus30mCheck(x)))).map[Subject](x => x.getClass().cast(x))

allow(allowedSubjects, "leaveFactory", allowedResources)
}

val accessFactoryRule = rules(accessFactory)
val checkTimeToWorkingPlaceRule = rules(checkTimeToWorkingPlace)
val giveOutEquipmentRule = rules(giveOutEquipment)
val enterWorkingPlaceRule = rules(enterWorkingPlace)
val leaveWorkingPlaceRule = rules(leaveWorkingPlace)
val leaveFactoryRule = rules(leaveFactory)


}
val rootEnsemble = root(new System)

val workerOnTheWay = false

def checkLocationOfWorker() : Boolean = {
  return workerOnTheWay
}

def shiftBeginMinus30mCheck(resource: Resource) : Boolean = {
  return true
}

def shiftBeginMinus30mCheck(subject: Subject) : Boolean = {
  return shiftCheck(subject, 30, false);
}

def shiftEndPlus10mCheck(resource: Resource) : Boolean = {
  return true
}

def shiftEndPlus10mCheck(subject: Subject) : Boolean = {
  return shiftCheck(subject, 10, true);
}

def shiftEndPlus30mCheck(resource: Resource) : Boolean = {
  return true
}

def shiftEndPlus30mCheck(subject: Subject) : Boolean = {
  return shiftCheck(subject, 30, true);
}

class Shift(val startTime : LocalTime, val endTime : LocalTime)

val shiftMap = new HashMap[String, Shift]
shiftMap.put("Early-Production", new Shift(LocalTime.parse("06:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME), LocalTime.parse("14:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME)))
shiftMap.put("Late-Production", new Shift(LocalTime.parse("15:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME), LocalTime.parse("23:00:00Z", DateTimeFormatter.ISO_OFFSET_TIME)))

def shiftCheck(subject: Subject, timeMin: Int, isPlus: Boolean) : Boolean = {
  if (isPlus) {
    val endTime = shiftMap.get(subject.shiftName).endTime
    val startTimeFit = now isAfter shiftMap.get(subject.shiftName).startTime
    return startTimeFit && ((now isBefore endTime.plusMinutes(timeMin)) || (now equals endTime.plusMinutes(timeMin)))
  }
  val time = shiftMap.get(subject.shiftName).startTime
  val endTimeFit = now isBefore shiftMap.get(subject.shiftName).endTime
  return endTimeFit && ((now isAfter time.minusMinutes(timeMin)) || (now equals time.minusMinutes(timeMin)))
}

}
object RunningExample {
def convertToCol(iterable: Iterable[Component]) : Collection[Component] = {
val collection = new ArrayList[Component]

val iter = iterable.iterator
while (iter.hasNext) {
collection.add(iter.next)
}

return collection
}

def main(args: Array[String]) : Unit = {
    val scenario = new RunningExample(LocalTime.parse("05:30:00Z", DateTimeFormatter.ISO_OFFSET_TIME))
    val subjectW = new scenario.Subject("W", "Factory", "Worker", "Late-Production")
    val subjectL = new scenario.Subject("L", "Production_Line", "Worker", "Late-Production")
    val subjectF = new scenario.Subject("F", "Factory", "Foreman", "Early-Production")
    val subjectX = new scenario.Subject("X", "Factory", "Worker", "Late-Production")
    scenario.components = List(subjectW, subjectL, subjectF, subjectX)
    scenario.rootEnsemble.init()
    val solved = scenario.rootEnsemble.solve()
    scenario.rootEnsemble.instance.accessFactoryRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));
    scenario.rootEnsemble.instance.checkTimeToWorkingPlaceRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));
    scenario.rootEnsemble.instance.giveOutEquipmentRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));
    scenario.rootEnsemble.instance.enterWorkingPlaceRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));
    val ok = solved && scenario.rootEnsemble.instance.accessFactoryRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty()) &&
                       scenario.rootEnsemble.instance.checkTimeToWorkingPlaceRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty()) &&
                       scenario.rootEnsemble.instance.giveOutEquipmentRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectW) && convertToCol(x.allowedSubjects).contains(subjectL) && !convertToCol(x.allowedSubjects).contains(subjectF) && convertToCol(x.allowedSubjects).contains(subjectX)) &&
                       scenario.rootEnsemble.instance.enterWorkingPlaceRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectL));
    
    val scenario2 = new RunningExample(LocalTime.parse("14:50:00Z", DateTimeFormatter.ISO_OFFSET_TIME))
    scenario2.components = List(subjectW, subjectL, subjectF, subjectX)
    scenario2.rootEnsemble.init()
    val solved2 = scenario2.rootEnsemble.solve()
    scenario2.rootEnsemble.instance.leaveWorkingPlaceRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));
    scenario2.rootEnsemble.instance.leaveFactoryRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));
    val ok2 = solved2 && scenario2.rootEnsemble.instance.leaveWorkingPlaceRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty()) &&
                         scenario2.rootEnsemble.instance.leaveFactoryRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty());                   
    if(ok && ok2) {
      println("OK")
    } else {
      println("NOT OK")
    }
}

}
