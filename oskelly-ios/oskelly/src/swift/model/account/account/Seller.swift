//
//  Seller.swift
//
//  Created by Виталий Хлудеев on 13.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Seller: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let bik = "bik"
    static let ogrn = "ogrn"
    static let city = "city"
    static let inn = "inn"
    static let address = "address"
    static let lastName = "lastName"
    static let correspondentAccount = "correspondentAccount"
    static let paymentAccount = "paymentAccount"
    static let passport = "passport"
    static let phone = "phone"
    static let ogrnip = "ogrnip"
    static let companyName = "companyName"
    static let firstName = "firstName"
    static let kpp = "kpp"
    static let zipCode = "zipCode"
  }

  // MARK: Properties
  public var bik: String?
  public var ogrn: String?
  public var city: String?
  public var inn: String?
  public var address: String?
  public var lastName: String?
  public var correspondentAccount: String?
  public var paymentAccount: String?
  public var passport: String?
  public var phone: String?
  public var ogrnip: String?
  public var companyName: String?
  public var firstName: String?
  public var kpp: String?
  public var zipCode: String?

  init() {}

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    bik = json[SerializationKeys.bik].string
    ogrn = json[SerializationKeys.ogrn].string
    city = json[SerializationKeys.city].string
    inn = json[SerializationKeys.inn].string
    address = json[SerializationKeys.address].string
    lastName = json[SerializationKeys.lastName].string
    correspondentAccount = json[SerializationKeys.correspondentAccount].string
    paymentAccount = json[SerializationKeys.paymentAccount].string
    passport = json[SerializationKeys.passport].string
    phone = json[SerializationKeys.phone].string
    ogrnip = json[SerializationKeys.ogrnip].string
    companyName = json[SerializationKeys.companyName].string
    firstName = json[SerializationKeys.firstName].string
    kpp = json[SerializationKeys.kpp].string
    zipCode = json[SerializationKeys.zipCode].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = bik { dictionary[SerializationKeys.bik] = value }
    if let value = ogrn { dictionary[SerializationKeys.ogrn] = value }
    if let value = city { dictionary[SerializationKeys.city] = value }
    if let value = inn { dictionary[SerializationKeys.inn] = value }
    if let value = address { dictionary[SerializationKeys.address] = value }
    if let value = lastName { dictionary[SerializationKeys.lastName] = value }
    if let value = correspondentAccount { dictionary[SerializationKeys.correspondentAccount] = value }
    if let value = paymentAccount { dictionary[SerializationKeys.paymentAccount] = value }
    if let value = passport { dictionary[SerializationKeys.passport] = value }
    if let value = phone { dictionary[SerializationKeys.phone] = value }
    if let value = ogrnip { dictionary[SerializationKeys.ogrnip] = value }
    if let value = companyName { dictionary[SerializationKeys.companyName] = value }
    if let value = firstName { dictionary[SerializationKeys.firstName] = value }
    if let value = kpp { dictionary[SerializationKeys.kpp] = value }
    if let value = zipCode { dictionary[SerializationKeys.zipCode] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.bik = aDecoder.decodeObject(forKey: SerializationKeys.bik) as? String
    self.ogrn = aDecoder.decodeObject(forKey: SerializationKeys.ogrn) as? String
    self.city = aDecoder.decodeObject(forKey: SerializationKeys.city) as? String
    self.inn = aDecoder.decodeObject(forKey: SerializationKeys.inn) as? String
    self.address = aDecoder.decodeObject(forKey: SerializationKeys.address) as? String
    self.lastName = aDecoder.decodeObject(forKey: SerializationKeys.lastName) as? String
    self.correspondentAccount = aDecoder.decodeObject(forKey: SerializationKeys.correspondentAccount) as? String
    self.paymentAccount = aDecoder.decodeObject(forKey: SerializationKeys.paymentAccount) as? String
    self.passport = aDecoder.decodeObject(forKey: SerializationKeys.passport) as? String
    self.phone = aDecoder.decodeObject(forKey: SerializationKeys.phone) as? String
    self.ogrnip = aDecoder.decodeObject(forKey: SerializationKeys.ogrnip) as? String
    self.companyName = aDecoder.decodeObject(forKey: SerializationKeys.companyName) as? String
    self.firstName = aDecoder.decodeObject(forKey: SerializationKeys.firstName) as? String
    self.kpp = aDecoder.decodeObject(forKey: SerializationKeys.kpp) as? String
    self.zipCode = aDecoder.decodeObject(forKey: SerializationKeys.zipCode) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(bik, forKey: SerializationKeys.bik)
    aCoder.encode(ogrn, forKey: SerializationKeys.ogrn)
    aCoder.encode(city, forKey: SerializationKeys.city)
    aCoder.encode(inn, forKey: SerializationKeys.inn)
    aCoder.encode(address, forKey: SerializationKeys.address)
    aCoder.encode(lastName, forKey: SerializationKeys.lastName)
    aCoder.encode(correspondentAccount, forKey: SerializationKeys.correspondentAccount)
    aCoder.encode(paymentAccount, forKey: SerializationKeys.paymentAccount)
    aCoder.encode(passport, forKey: SerializationKeys.passport)
    aCoder.encode(phone, forKey: SerializationKeys.phone)
    aCoder.encode(ogrnip, forKey: SerializationKeys.ogrnip)
    aCoder.encode(companyName, forKey: SerializationKeys.companyName)
    aCoder.encode(firstName, forKey: SerializationKeys.firstName)
    aCoder.encode(kpp, forKey: SerializationKeys.kpp)
    aCoder.encode(zipCode, forKey: SerializationKeys.zipCode)
  }

}
