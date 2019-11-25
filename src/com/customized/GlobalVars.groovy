#!/usr/bin/env groovy
package com.customized

class GlobalVars {
private String jobName
@NonCPS
def tupleFunc(String name){
jobName = name
String jobname = "$jobName"
def tag;
//def JOB_NAME = "java-ms1/release-test123_pipeline"
def folder = jobname.split("/")[0]
def job = jobname.split("/")[-1]
def branching = job.split("_")[0]
def branchname = branching
def tagging; 
      switch (branchname) {
         case ~/develop.*/: tag = "dev"; break;
         case ~/feature.*/: tag = "dev"; break;
         case ~/release.*/: tag = "uat"; break;
         case ~/master.*/:  tag = "prod"; break;
      }
}
}