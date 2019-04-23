/**
 *  Porch Lights With Motion
 *
 *  Copyright 2019 Josh Fletcher
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
  name: "Porch Lights With Motion",
  namespace: "Bigtuna00",
  author: "Josh Fletcher",
  description: "Prevent infinite lighting loops when using Ring Doorbell motion sensor to turn lights on.",
  category: "Convenience",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  section("When there’s movement...") {
    input "motion1", "capability.motionSensor", title: "Where?", required: true, multiple: true
  }
  section("Turn on a light...") {
    input "switch1", "capability.switch", required: true, multiple: true
  }
  section("Turn on between what times?") {
    input "fromTime", "time", title: "From", required: true
    input "toTime", "time", title: "To", required: true
  }
}

def installed() {
  initialize()
}

def updated() {
  unsubscribe()
  initialize()
}

def initialize() {
  subscribeToMotion()
}

def motionActiveHandler(evt) {
  log.debug "Motion detected"
  if (isactive()) {
    switch1.on()
    runIn(60 * 5, lightsOffIgnoreMotion)
  }
}

def lightsOffIgnoreMotion() {
  log.debug "lightsOffIgnoreMotion"
  if (isactive()) {
    unsubscribe(motion1)
    switch1.off()
    runIn(15, subscribeToMotion)
  }
}

def subscribeToMotion() {
  log.debug "subscribeToMotion"
  subscribe(motion1, "motion.active", motionActiveHandler)
}

def isactive() {
  def between = timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)
  log.debug "Active? ${between}"
  return between
}
