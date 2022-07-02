//
//  DeliveryRequisite.swift
//
//  Created by Виталий Хлудеев on 31.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class DeliveryRequisite: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let deliveryZipCode = "deliveryZipCode"
    static let deliveryCity = "deliveryCity"
    static let deliveryName = "deliveryName"
    static let deliveryAddress = "deliveryAddress"
    static let deliveryCountry = "deliveryCountry"
    static let deliveryPhone = "deliveryPhone"
  }

  // MARK: Properties
  public var deliveryZipCode: String?
  public var deliveryCity: String?
  public var deliveryName: String?
  public var deliveryAddress: String?
  public var deliveryCountry: String?
  public var deliveryPhone: String?

  init() {

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
    deliveryZipCode = json[SerializationKeys.deliveryZipCode].string
    deliveryCity = json[SerializationKeys.deliveryCity].string
    deliveryName = json[SerializationKeys.deliveryName].string
    deliveryAddress = json[SerializationKeys.deliveryAddress].string
    deliveryCountry = json[SerializationKeys.deliveryCountry].string
    deliveryPhone = json[SerializationKeys.deliveryPhone].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = deliveryZipCode { dictionary[SerializationKeys.deliveryZipCode] = value }
    if let value = deliveryCity { dictionary[SerializationKeys.deliveryCity] = value }
    if let value = deliveryName { dictionary[SerializationKeys.deliveryName] = value }
    if let value = deliveryAddress { dictionary[SerializationKeys.deliveryAddress] = value }
    if let value = deliveryCountry { dictionary[SerializationKeys.deliveryCountry] = value }
    if let value = deliveryPhone { dictionary[SerializationKeys.deliveryPhone] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.deliveryZipCode = aDecoder.decodeObject(forKey: SerializationKeys.deliveryZipCode) as? String
    self.deliveryCity = aDecoder.decodeObject(forKey: SerializationKeys.deliveryCity) as? String
    self.deliveryName = aDecoder.decodeObject(forKey: SerializationKeys.deliveryName) as? String
    self.deliveryAddress = aDecoder.decodeObject(forKey: SerializationKeys.deliveryAddress) as? String
    self.deliveryCountry = aDecoder.decodeObject(forKey: SerializationKeys.deliveryCountry) as? String
    self.deliveryPhone = aDecoder.decodeObject(forKey: SerializationKeys.deliveryPhone) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(deliveryZipCode, forKey: SerializationKeys.deliveryZipCode)
    aCoder.encode(deliveryCity, forKey: SerializationKeys.deliveryCity)
    aCoder.encode(deliveryName, forKey: SerializationKeys.deliveryName)
    aCoder.encode(deliveryAddress, forKey: SerializationKeys.deliveryAddress)
    aCoder.encode(deliveryCountry, forKey: SerializationKeys.deliveryCountry)
    aCoder.encode(deliveryPhone, forKey: SerializationKeys.deliveryPhone)
  }

}
