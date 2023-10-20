//
//  AppDelegate.swift
//  OnRewind
//
//  Created by Sergei Mikhan on 4/3/18.
//  Copyright © 2018 Sergei Mikhan. All rights reserved.
//

import UIKit
import Astrarium

import OnRewindSDK
import Dioptra

#if SPORTBUFF_ENABLED
import SportBuff
#endif

import Gnomon

import onrewindshared

@UIApplicationMain

public class AppDelegate: Astrarium.AppDelegate {

  public override var services: [ServiceIds?] { return [
    .ui
    ]
  }

  public override init() {
    super.init()
    OnRewind.set(receiverAppID: "A4066AA6")
    OnRewind.setNPAW(accountCode: "azd")
    Gnomon.logging = true
  }

  public var window: UIWindow? {
    get { return Services[.ui]?.window }
    set { assertionFailure("window setter should not be called directly") }
  }
}
