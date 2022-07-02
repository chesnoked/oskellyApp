//
//  SellerRequisite.swift
//
//  Created by Виталий Хлудеев on 20.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class SellerRequisite: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let city = "city"
    static let fullName = "fullName"
    static let address = "address"
    static let phone = "phone"
    static let lastName = "lastName"
    static let firstName = "firstName"
    static let zipCode = "zipCode"
  }

  // MARK: Properties
  public var city: String?
  public var fullName: String?
  public var address: String?
  public var phone: String?
  public var lastName: String?
  public var firstName: String?
  public var zipCode: String?

  public init() {

  }

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
    city = json[SerializationKeys.city].string
    fullName = json[SerializationKeys.fullName].string
    address = json[SerializationKeys.address].string
    phone = json[SerializationKeys.phone].string
    lastName = json[SerializationKeys.lastName].string
    firstName = json[SerializationKeys.firstName].string
    zipCode = json[SerializationKeys.zipCode].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = city { dictionary[SerializationKeys.city] = value }
    if let value = fullName { dictionary[SerializationKeys.fullName] = value }
    if let value = address { dictionary[SerializationKeys.address] = value }
    if let value = phone { dictionary[SerializationKeys.phone] = value }
    if let value = lastName { dictionary[SerializationKeys.lastName] = value }
    if let value = firstName { dictionary[SerializationKeys.firstName] = value }
    if let value = zipCode { dictionary[SerializationKeys.zipCode] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.city = aDecoder.decodeObject(forKey: SerializationKeys.city) as? String
    self.fullName = aDecoder.decodeObject(forKey: SerializationKeys.fullName) as? String
    self.address = aDecoder.decodeObject(forKey: SerializationKeys.address) as? String
    self.phone = aDecoder.decodeObject(forKey: SerializationKeys.phone) as? String
    self.lastName = aDecoder.decodeObject(forKey: SerializationKeys.lastName) as? String
    self.firstName = aDecoder.decodeObject(forKey: SerializationKeys.firstName) as? String
    self.zipCode = aDecoder.decodeObject(forKey: SerializationKeys.zipCode) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(city, forKey: SerializationKeys.city)
    aCoder.encode(fullName, forKey: SerializationKeys.fullName)
    aCoder.encode(address, forKey: SerializationKeys.address)
    aCoder.encode(phone, forKey: SerializationKeys.phone)
    aCoder.encode(lastName, forKey: SerializationKeys.lastName)
    aCoder.encode(firstName, forKey: SerializationKeys.firstName)
    aCoder.encode(zipCode, forKey: SerializationKeys.zipCode)
  }

}
