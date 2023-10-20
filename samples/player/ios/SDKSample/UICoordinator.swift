//
//  UICoordinator.swift
//  OnRewind
//
//  Created by Dmitry Duleba on 6/14/18.
//  Copyright © 2018 NetcoSports. All rights reserved.
//

import Astrarium

import RxSwift
import RxCocoa
import Astrolabe

import Alidade
import OnRewindSDK

class NavigationController: UINavigationController {

  override open var supportedInterfaceOrientations: UIInterfaceOrientationMask {
    return isPad ? .landscape : .portrait
  }
}

extension ServiceIds {

  //swiftlint:disable:next identifier_name
  static let ui = ServiceIdentifier<UICoordinator>()
}

var isPad: Bool { return UIDevice.current.userInterfaceIdiom == .pad }
var isPortrait: Bool { return UIApplication.shared.statusBarOrientation.isPortrait }

final class UICoordinator: AppService {

  let window = UIWindow(frame: UIScreen.main.bounds)
  private let disposeBag = DisposeBag()

  // MARK: - Lifecycle

  func setup(with launchOptions: LaunchOptions) {
    window.backgroundColor = .black
    let viewController: UIViewController
//    #if DEBUG
//    viewController = controllerFor(baseUrl: "https://api-gateway.onrewind.tv/main-api", eventId: "10b3c2b1-e132-4046-aa13-82dd5dda3375")
//    //viewController = controllerFor(baseUrl: "https://dev-api-gateway.onrewind.tv/main-api", eventId: "64fa098b-95ec-49bf-aafd-39b56c6b402d")
//    #else
    let home = HomeViewController()
    viewController = NavigationController(rootViewController: home)
//    #endif
    window.rootViewController = viewController
    window.makeKeyAndVisible()
  }
}
