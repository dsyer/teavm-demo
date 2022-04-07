#include "external/cJSON.h"

cJSON *read(char *raw)
{
	return cJSON_Parse(raw);
}

char *write(cJSON *json)
{
	return cJSON_Print(json);
}

void del(cJSON *json)
{
	cJSON_Delete(json);
}

cJSON *create(void)
{
	char *string = NULL;
	cJSON *message = NULL;
	size_t index = 0;

	cJSON *hello = cJSON_CreateObject();
	if (hello == NULL)
	{
		goto end;
	}

	message = cJSON_CreateString("Hello World");
	if (message == NULL)
	{
		goto end;
	}
	cJSON_AddItemToObject(hello, "message", message);

end:
	return hello;
}

char *render()
{
	cJSON *hello = create();
	char *string = string = cJSON_Print(hello);
	if (string == NULL)
	{
		return NULL;
	}
end:
	cJSON_Delete(hello);
	return string;
}