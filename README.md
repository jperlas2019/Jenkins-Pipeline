# Jenkins-Pipeline
 
A Jenkins Pipeline that automatically deploys a Docker image of a Python application with the following stages:
- Build
- Lint
- Test & Coverage
- Package (Push to Docker Hub)
- Zip Artificats
- Deliver (Run the Docker image)

In this academic project, Jenkins and GitLab are installed "on-premises" on Azure VMs. The following files included in this repository are the files used to demonstrate a working Jenkins Pipeline.
This Jenkins also uses a webhook connected to GitLab to automatically build the GitLab repository on each commit and merge request.

This project demonstrates the following skills:
- Installing and configuring on-premise Jenkins and GitLab
- Shell scripting and Python
- Ubuntu
- Git, GitLab
- Implementing Docker and building Docker images
- Jenkins shared libraries
- Groovy language
- Ability to setup and configure a Jenkins Pipeline
- General DevOps skills and CI/CD methodologies
