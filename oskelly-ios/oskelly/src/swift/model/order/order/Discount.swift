//
//  Discount.swift
//
//  Created by Виталий Хлудеев on 23.01.18
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Discount: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let finalOrderAmount = "finalOrderAmount"
    static let discountValue = "discountValue"
    static let code = "code"
    static let savingsValue = "savingsValue"
    static let isValidYet = "isValidYet"
    static let updatedOrderAmount = "updatedOrderAmount"
    static let optionalText = "optionalText"
  }

  // MARK: Properties
  public var finalOrderAmount: String?
  public var discountValue: String?
  public var code: String?
  public var savingsValue: String?
  public var isValidYet: Bool? = false
  public var updatedOrderAmount: String?
  public var optionalText: String?

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
    finalOrderAmount = json[SerializationKeys.finalOrderAmount].string
    discountValue = json[SerializationKeys.discountValue].string
    code = json[SerializationKeys.code].string
    savingsValue = json[SerializationKeys.savingsValue].string
    isValidYet = json[SerializationKeys.isValidYet].boolValue
    updatedOrderAmount = json[SerializationKeys.updatedOrderAmount].string
    optionalText = json[SerializationKeys.optionalText].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = finalOrderAmount { dictionary[SerializationKeys.finalOrderAmount] = value }
    if let value = discountValue { dictionary[SerializationKeys.discountValue] = value }
    if let value = code { dictionary[SerializationKeys.code] = value }
    if let value = savingsValue { dictionary[SerializationKeys.savingsValue] = value }
    dictionary[SerializationKeys.isValidYet] = isValidYet
    if let value = updatedOrderAmount { dictionary[SerializationKeys.updatedOrderAmount] = value }
    if let value = optionalText { dictionary[SerializationKeys.optionalText] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.finalOrderAmount = aDecoder.decodeObject(forKey: SerializationKeys.finalOrderAmount) as? String
    self.discountValue = aDecoder.decodeObject(forKey: SerializationKeys.discountValue) as? String
    self.code = aDecoder.decodeObject(forKey: SerializationKeys.code) as? String
    self.savingsValue = aDecoder.decodeObject(forKey: SerializationKeys.savingsValue) as? String
    self.isValidYet = aDecoder.decodeBool(forKey: SerializationKeys.isValidYet)
    self.updatedOrderAmount = aDecoder.decodeObject(forKey: SerializationKeys.updatedOrderAmount) as? String
    self.optionalText = aDecoder.decodeObject(forKey: SerializationKeys.optionalText) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(finalOrderAmount, forKey: SerializationKeys.finalOrderAmount)
    aCoder.encode(discountValue, forKey: SerializationKeys.discountValue)
    aCoder.encode(code, forKey: SerializationKeys.code)
    aCoder.encode(savingsValue, forKey: SerializationKeys.savingsValue)
    aCoder.encode(isValidYet, forKey: SerializationKeys.isValidYet)
    aCoder.encode(updatedOrderAmount, forKey: SerializationKeys.updatedOrderAmount)
    aCoder.encode(optionalText, forKey: SerializationKeys.optionalText)
  }

}
