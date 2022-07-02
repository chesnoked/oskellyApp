//
//  PaymentRequest.swift
//
//  Created by Виталий Хлудеев on 01.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class PaymentRequest: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let request = "request"
    static let card = "card"
  }

  // MARK: Properties
  public var request: String?
  public var card: Card?
  public var d3SecureData: Any?

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
    request = json[SerializationKeys.request].string
    card = Card(json: json[SerializationKeys.card])
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = request { dictionary[SerializationKeys.request] = value }
    if let value = card { dictionary[SerializationKeys.card] = value.dictionaryRepresentation() }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.request = aDecoder.decodeObject(forKey: SerializationKeys.request) as? String
    self.card = aDecoder.decodeObject(forKey: SerializationKeys.card) as? Card
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(request, forKey: SerializationKeys.request)
    aCoder.encode(card, forKey: SerializationKeys.card)
  }

}
