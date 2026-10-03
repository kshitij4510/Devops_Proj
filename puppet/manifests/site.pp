# ==============================================================================
# Puppet Manifest: Farm Equipment Rental System Host Configuration
# Node: Production & Staging Target Environments
# ==============================================================================

node default {

  $app_name      = 'farm-equipment-rental'
  $app_user      = 'agrirent'
  $app_group     = 'agrirent'
  $app_root      = '/opt/agrirent'
  $target_port   = 8085
  $jar_file      = 'farm-equipment-rental-1.0.0.jar'

  # 1. System Group
  group { $app_group:
    ensure => present,
    system => true,
  }

  # 2. System User
  user { $app_user:
    ensure     => present,
    gid        => $app_group,
    home       => $app_root,
    shell      => '/bin/false',
    system     => true,
    managehome => false,
    require    => Group[$app_group],
  }

  # 3. Directory Hierarchy
  $app_dirs = [
    $app_root,
    "${app_root}/bin",
    "${app_root}/config",
    "${app_root}/logs",
    "${app_root}/data",
  ]

  file { $app_dirs:
    ensure  => directory,
    owner   => $app_user,
    group   => $app_group,
    mode    => '0755',
    require => User[$app_user],
  }

  # 4. Runtime Configuration File
  file { "${app_root}/config/application.properties":
    ensure  => file,
    owner   => $app_user,
    group   => $app_group,
    mode    => '0640',
    content => @("EOF")
      server.port=${target_port}
      spring.application.name=${app_name}
      spring.profiles.active=prod
      management.endpoints.web.exposure.include=health,info,metrics
      logging.file.name=${app_root}/logs/agrirent.log
      | EOF
    require => File["${app_root}/config"],
    notify  => Service['agrirent'],
  }

  # 5. Service Definition
  file { '/etc/systemd/system/agrirent.service':
    ensure  => file,
    owner   => 'root',
    group   => 'root',
    mode    => '0644',
    content => @("EOF")
      [Unit]
      Description=AgriRent Farm Equipment Rental System
      After=network.target

      [Service]
      Type=simple
      User=${app_user}
      Group=${app_group}
      WorkingDirectory=${app_root}
      ExecStart=/usr/bin/java -jar ${app_root}/bin/${jar_file} --server.port=${target_port}
      Restart=always
      RestartSec=10

      [Install]
      WantedBy=multi-user.target
      | EOF
    notify  => Service['agrirent'],
  }

  # 6. Service Management
  service { 'agrirent':
    ensure     => running,
    enable     => true,
    hasstatus  => true,
    hasrestart => true,
    require    => [
      File['/etc/systemd/system/agrirent.service'],
      File["${app_root}/config/application.properties"],
    ],
  }
}
