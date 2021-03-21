import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.2"

project {
    description = "Contains all other projects"

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject(ExampleTeamcityDz)
}


object ExampleTeamcityDz : Project({
    name = "Example Teamcity DZ"

    vcsRoot(ExampleTeamcityDz_HttpsGithubComOlegAnanyevExampleTeamcityRefsHeadsMaster)

    buildType(ExampleTeamcityDz_Build)
})

object ExampleTeamcityDz_Build : BuildType({
    name = "Build"

    vcs {
        root(ExampleTeamcityDz_HttpsGithubComOlegAnanyevExampleTeamcityRefsHeadsMaster)
    }

    steps {
        maven {
            name = "Package"

            conditions {
                equals("teamcity.build.branch", "master")
            }
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "Test"

            conditions {
                doesNotEqual("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }
})

object ExampleTeamcityDz_HttpsGithubComOlegAnanyevExampleTeamcityRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/OlegAnanyev/example-teamcity#refs/heads/master"
    url = "https://github.com/OlegAnanyev/example-teamcity"
    branch = "master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "OlegAnanyev"
        password = "credentialsJSON:46726796-d304-4570-b7a3-3f735092cd56"
    }
})
