package lime.system;

#if (!lime_doc_gen || (android && lime_cffi))
import lime._internal.backend.native.NativeCFFI;
import lime.utils.Bytes;

#if !lime_debug
@:fileXml('tags="haxe,release"')
@:noDebug
#end
@:access(lime._internal.backend.native.NativeCFFI)
class DocumentSystem {

	@:noCompletion
	private var handle:Dynamic;

    public function new(treeUri:String) {
        handle = NativeCFFI.lime_documentsystem_create(treeUri);
    }

	public function writeBytes(path:String, bytes:Bytes):Void
	{
		NativeCFFI.lime_documentsystem_write_bytes(handle, path, bytes);
	}

	public function readBytes(path:String):Bytes
	{
		var bytes:Bytes = Bytes.alloc(0);
		NativeCFFI.lime_documentsystem_read_bytes(handle, path, bytes);
		if (bytes.length == 0) {
			return null;
		}
		return bytes;
	}

	public function createDirectory(path:String):Void
	{
		NativeCFFI.lime_documentsystem_create_directory(handle, path);
	}

	public function readDirectory(path:String):Array<String>
	{
		return NativeCFFI.lime_documentsystem_read_directory(handle, path);
	}

	public function exists(path:String):Bool
	{
		return NativeCFFI.lime_documentsystem_exists(handle, path);
	}

	public function deleteDirectory(path:String):Bool
	{
		return NativeCFFI.lime_documentsystem_delete_directory(handle, path);
	}

	public function deleteFile(path:String):Bool
	{
		return NativeCFFI.lime_documentsystem_delete_file(handle, path);
	}

	public function isDirectory(path:String):Bool
	{
		return NativeCFFI.lime_documentsystem_is_directory(handle, path);
	}
}
#end