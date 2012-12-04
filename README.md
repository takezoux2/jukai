# Jukai

Jukai is AWS library for scala.


## Current support

- S3
- SimpleDB



## S3

    import com.geishatokyo.jukai.factory.Region

    val region = Region.fromName("tokyo")
    val s3Connection = region.withCredentials("YourAccessKey","YourSecretKey").s3

    val bucket = s3Connection.bucket("S3BucketName")
    bucket.createBucket()

    bucket += ( "Key.jpg" -> data_as_ByteArray)

    val data = bucket("Key")

    bucket.deleteBucket()

- ContentType is guessed by key's file extention.( image/jpeg will set in above case)
- ACL is set to "Can read/download from everyone" by default.(CannedAccessControlList.PublicRead)


## SimpleDB

    import com.geishatokyo.jukai.factory.Region
    import com.geishatokyo.jukai.simpledb.SimpleDBImplicits._

    val region = Region.fromName("tokyo")
    val simpleDbConnection = region.withCredentials("YourAccessKey","YourSecretKey").simpleDb

    val domain = simpleDbConnection.domain("YourSimpleDBDomain")
    domain.createDomain()

    //put
    domain.put("Key" -> "FieldName" -> "FieldValue") // set single value
    domain.put("Key2"-> Map("Field1" -> "V1","Field2" -> "V2)) // set multi values
    domain.put("Key3")("Field1" -> "V1","Field2" -> "V2") // no SimpleDBImplicits
    domain.put("Key4" -> "FieldName" -> "FieldValue" when( "ExsitFieldName" === "OK")) // conditioning
    domain.put("Key4" -> "FieldName" -> "FieldValue" when( "ExsitFieldName" exist)) // conditioning

    //get

    domain.get("Key") match{
      case AWSSuccess(values,response) => values === Map("FieldName" -> "FieldValue")
    }
    domain.getOne("Key" -> "FieldName" consistently) === "FieldValue") // get only one field.

