//
//  AppDelegate.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 27.04.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import UIKit
import Fabric
import Crashlytics
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        Fabric.with([Crashlytics.self])
        if(GlobalProvider.instance.getAccountManager().isUserLoggedIn()) {
            Crashlytics.sharedInstance().setUserEmail(GlobalProvider.instance.getAccountManager().getEmail())
        }
        registerForPushNotifications(application: application)

        let mainStoryboard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
//        GlobalProvider.instance.getAccountManager().eraseCredentials()
        if(GlobalProvider.instance.getAccountManager().isUserLoggedIn())
        {
            let initialViewController = mainStoryboard.instantiateViewController(withIdentifier: "ApplicationTabBarController") as! UITabBarController
            self.window?.rootViewController = initialViewController
            self.window?.makeKeyAndVisible()

            if let option = launchOptions {
                let info = option[UIApplicationLaunchOptionsKey.remoteNotification]
                if (info != nil) {
                    initialViewController.selectedIndex = 3 // экран уведомлений
                }
            }
            GlobalProvider.instance.synchronizeData()
        }
        UIBarButtonItem.appearance().setBackButtonTitlePositionAdjustment(UIOffsetMake(-350, 0), for:UIBarMetrics.default)

        let attributes = [NSForegroundColorAttributeName : AppColors.lato()]
        UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self]).setTitleTextAttributes(attributes, for: .normal)
        UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self]).title = "Отмена"

        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        var token = ""
        for i in 0..<deviceToken.count {
            token = token + String(format: "%02.2hhx", arguments: [deviceToken[i]])
        }
        print(token)
        GlobalProvider.instance.getAccountManager().setDeviceToken(deviceToken: token)

        if(GlobalProvider.instance.getAccountManager().isUserLoggedIn()) { // если пользователь включает оповещения уже после того как вошел в приложение
            GlobalProvider.instance.getAccountManager().sendDeviceToken()
        }
    }

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register:", error)
    }

    // see https://stackoverflow.com/questions/40991477/didregisterforremotenotificationswithdevicetoken-not-getting-called-on-ios-10
    func registerForPushNotifications(application: UIApplication) {

        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().delegate = self
            UNUserNotificationCenter.current().requestAuthorization(options: [.badge, .sound, .alert], completionHandler: {(granted, error) in
                if (granted) {
                    UIApplication.shared.registerForRemoteNotifications()
                }
                if let e = error {
                    print("Failed to granted:", e)
                }
            })
        }
        else {
            let notificationSettings = UIUserNotificationSettings(
                    types: [.badge, .sound, .alert], categories: nil)
            application.registerUserNotificationSettings(notificationSettings)
        }
    }
}

