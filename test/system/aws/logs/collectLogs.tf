/**
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 */

provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region = "${var.aws_region}"
}

resource "aws_instance" "swarm_master" {
  ami = "${lookup(var.pravega_aws_amis, var.aws_region)}"
  instance_type = "${lookup(var.pravega_instance_type, var.aws_region)}"
  key_name = "${var.aws_key_name}"
  vpc_security_group_ids = ["pravega_default"]

  provisioner "remote-exec" {
    connection = {
      type = "ssh"
      user = "ubuntu"
      private_key = "${file("${var.cred_path}")}"
    }
    inline = [
      "chmod +x /tmp/logTarScript.sh",
      "/tmp/logTarScript.sh ${var.pravega_org} ${var.pravega_branch}"
    ]
  }
}
resource "aws_instance" "swarm_slaves" {
  ami = "${lookup(var.pravega_aws_amis, var.aws_region)}"
  instance_type = "${lookup(var.pravega_instance_type, var.aws_region)}"
  key_name = "${var.aws_key_name}"
  vpc_security_group_ids = ["pravega_default"]

  provisioner "remote-exec" {
    connection = {
      type = "ssh"
      user = "ubuntu"
      private_key = "${file("${var.cred_path}")}"
    }
    inline = [
      "chmod +x /tmp/logTarScript.sh",
      "/tmp/logTarScript.sh ${var.pravega_org} ${var.pravega_branch}",
    ]
  }
}
